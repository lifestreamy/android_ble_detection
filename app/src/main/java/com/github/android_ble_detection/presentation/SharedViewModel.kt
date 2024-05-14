package com.github.android_ble_detection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.android_ble_detection.data.bluetooth.*
import com.github.android_ble_detection.domain.model.BluetoothLeDevice
import com.github.android_ble_detection.presentation.SharedScreenEvent.*
import com.github.android_ble_detection.presentation.SharedViewModel.DeviceProximityState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@HiltViewModel
class SharedViewModel @Inject constructor(private val blController: BluetoothLEController) :
    ViewModel() {

    private val mapKalmanFilters: HashMap<String, KalmanFilter> = hashMapOf()

    private var processingScansJob: Job? = null

    private var deviceNotificationsJob: Job? = null

    private var lastDeviceDistance: Int? = null // last recorded device distance

    private var lastReportedState: DeviceProximityState = STAYED_OUTSIDE_RANGE

    private sealed class DeviceProximityState(val msg: String) {
        data object ENTERED_RANGE : DeviceProximityState("The device ENTERED the range" + msgPostfix)
        data object EXITED_RANGE : DeviceProximityState("The device EXITED the range" + msgPostfix)
        data object STAYED_INSIDE_RANGE : DeviceProximityState("The device is still INSIDE range" + msgPostfix)
        data object STAYED_OUTSIDE_RANGE : DeviceProximityState("The device is still OUTSIDE range" + msgPostfix)

        private companion object {
            val msgPostfix = "\n of ${BleConstants.defaultThresholdDistance} cm"
        }
    }

    private fun onDeviceAtDistance(newDistance: Int) {
        //if device comes in range notify (device in distance)
        //every second in distance notify (X seconds in distance)
        //when device is Y seconds in distance notify "action triggered"
        // if moved away notify "device moved away"

        val reportedState: DeviceProximityState

        if (lastDeviceDistance == null) {
            lastDeviceDistance = newDistance
            if (newDistance <= BleConstants.defaultThresholdDistance) emitMessage(ENTERED_RANGE.msg)
            return
        } else {
            val wasInRange = lastDeviceDistance!! <= BleConstants.defaultThresholdDistance
            val isInRange = newDistance <= BleConstants.defaultThresholdDistance

            reportedState = when (isInRange) {
                true -> if (wasInRange) STAYED_INSIDE_RANGE else ENTERED_RANGE
                false -> if (wasInRange) EXITED_RANGE else STAYED_OUTSIDE_RANGE
            }

            if (lastReportedState == reportedState) return

            when (reportedState) {
                ENTERED_RANGE -> emitMessage(ENTERED_RANGE.msg)
                EXITED_RANGE -> emitMessage(EXITED_RANGE.msg)
                STAYED_INSIDE_RANGE -> emitMessage(STAYED_INSIDE_RANGE.msg)
                STAYED_OUTSIDE_RANGE -> {/* not needed to report that device is outside*/
                }
            }

            lastDeviceDistance = newDistance
            lastReportedState = reportedState
        }
    }


    private val errors = blController.errors

    private fun emitMessage(m: String) = viewModelScope.launch { snackbarMessages.emit(m) }

    private val snackbarMessages: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val snackbarNotifications = merge(
        errors,
        snackbarMessages
    )


    private val _screenState: MutableStateFlow<SharedScreenState> =
        MutableStateFlow(SharedScreenState())
    val screenState: StateFlow<SharedScreenState> = _screenState.asStateFlow()


    fun onEvent(event: SharedScreenEvent) {
        when (event) {
            StartScanForAllDevices -> startScanForAllDevices()
            StartScanForMyDevices -> startScanForMyDevices()
            StopScan -> stopScan()
            ClearDevicesList -> clearBufferedDevices()
        }

    }

    private fun startScanForAllDevices() {
        blController.startScanForAllDevices(isScanStarted = { started ->
            if (started) startProcessingScans()
        })
    }

    private fun startScanForMyDevices() {
        blController.startScanForMyDevices(isScanStarted = { started ->
            if (started) startProcessingScans()
        })
    }

    private fun stopScan() {
        if (blController.stopScan()) stopProcessingScans()
    }

    private fun clearBufferedDevices() {
        blController.clearDevicesMap()
        _screenState.update { it.copy(scannedDevices = emptyList()) }
    }

    private fun getSmoothedDeviceDistance(device: BluetoothLeDevice): Int {
        if (device.address !in mapKalmanFilters) mapKalmanFilters[device.address] =
            KalmanFilter.default
        val kalmanFilter = mapKalmanFilters[device.address]!!
        val estimatedDistance =
            kalmanFilter.applyFilter(measurement = device.signalStrength.toDouble()).absoluteValue.calculateDistanceFromRssi()
                .times(100).roundToInt()
        return estimatedDistance
    }

    private fun startProcessingScans() {
        if (processingScansJob == null) processingScansJob =
            blController.scannedDevicesListFlow.distinctUntilChanged().onEach { devices ->
                val smoothedDevices = devices.map { device ->
                    val estimatedDistance = getSmoothedDeviceDistance(device)
                    val resultDevice = device.copy(estimatedDistance = estimatedDistance)

                    if (resultDevice.address == BleConstants.myXiaomiMac) onDeviceAtDistance(estimatedDistance)

                    resultDevice
                }
                _screenState.update { it.copy(scannedDevices = smoothedDevices) }
            }.flowOn(Dispatchers.Default).launchIn(viewModelScope)
        else emitMessage("Cannot start processing scans job,\nalready started")
    }

    private fun stopProcessingScans() {
        processingScansJob?.cancel()
        processingScansJob = null
    }


}

data class SharedScreenState(
    val scannedDevices: List<BluetoothLeDevice> = emptyList()
)

sealed interface SharedScreenEvent {
    data object StartScanForAllDevices : SharedScreenEvent
    data object StartScanForMyDevices : SharedScreenEvent
    data object StopScan : SharedScreenEvent
    data object ClearDevicesList : SharedScreenEvent

}