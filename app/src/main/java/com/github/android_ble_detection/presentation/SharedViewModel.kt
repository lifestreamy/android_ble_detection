package com.github.android_ble_detection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.android_ble_detection.data.bluetooth.BluetoothLEController
import com.github.android_ble_detection.domain.model.BluetoothLeDevice
import com.github.android_ble_detection.presentation.SharedScreenEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(private val blController: BluetoothLEController) :
    ViewModel() {

    init {
        blController.scannedDevicesListFlow.onEach { newDevices ->
            _screenState.update { it.copy(scannedDevices = newDevices) }
        }.launchIn(viewModelScope)
    }

    val errors = blController.errors

    private val _screenState: MutableStateFlow<SharedScreenState> =
        MutableStateFlow(SharedScreenState())
    val screenState: StateFlow<SharedScreenState> = _screenState.asStateFlow()

    fun onEvent(event: SharedScreenEvent) {
        when (event) {
            StartScanForAllDevices -> startScanForAllDevices()
            StartScanForMyDevices -> startScanForMyDevices()
            StopScan -> stopScan()
            ClearDevicesList -> clearDevicesList()
        }

    }

    private fun startScanForAllDevices(duration: Long? = null) =
        blController.startScanForAllDevices(duration)

    private fun startScanForMyDevices(duration: Long? = null) =
        blController.startScanForMyDevices(duration)

    private fun stopScan() = blController.stopScan()

    private fun clearDevicesList() = _screenState.update { it.copy(scannedDevices = emptyList()) }

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