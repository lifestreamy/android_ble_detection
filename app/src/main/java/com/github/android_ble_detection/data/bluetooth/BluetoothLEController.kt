package com.github.android_ble_detection.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import androidx.core.content.getSystemService
import com.github.android_ble_detection.core.util.cancelChildren
import com.github.android_ble_detection.domain.model.BluetoothLeDevice
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

@SuppressLint("MissingPermission")
class BluetoothLEController(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _errors: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val errors = _errors.asSharedFlow()

    private val _isInitialized = MutableStateFlow(false)
    private var isInitialized: Boolean
        get() = _isInitialized.value
        set(value) = _isInitialized.update { value }


    private val _isScanning = MutableStateFlow(false)
    private var isScanning: Boolean
        get() = _isScanning.value
        set(value) = _isScanning.update { value }


    private var blManager: BluetoothManager? = null
    private var blAdapter: BluetoothAdapter? = null
    private var bleScanner: BluetoothLeScanner? = null


    private val defaultScanCallback: ScanCallback =
        DefaultScanCallback(singleDeviceScanCallback = { callbackType, device ->
            scope.launch {
                when (callbackType) {
                    SingleCallbackType.ALL_MATCHES -> {
                        _scannedDevicesMap.update { map ->
                            map.toMutableMap().apply {
                                put(
                                    device.address,
                                    device
                                )
                            }
                        }
                    }
                    SingleCallbackType.FIRST_MATCH -> { /* triggers only for the first packet from device passing filter  */
                    }
                    SingleCallbackType.MATCH_LOST -> {
                        _scannedDevicesMap.update { map ->
                            map.toMutableMap().apply { remove(device.address) }
                        }
                    }
                    SingleCallbackType.UNKNOWN -> {/* for 'when' statement exhaustion, should not occur*/
                        throw Exception("SingleCallbackType.UNKNOWN found")
                    }
                }
            }
        },
            onBatchFlush = { list ->
                _scannedDevicesMap.update {
                    list.associateBy {
                        it.address
                    }
                }
            },
            onScanFailed = { scanExc ->
                scope.launch {
                    _errors.emit(scanExc.msg)
                }
            })


    private val _scannedDevicesMap: MutableStateFlow<Map<String, BluetoothLeDevice>> =
        MutableStateFlow(emptyMap())
    val scannedDevicesListFlow: Flow<List<BluetoothLeDevice>> =
        _scannedDevicesMap.map { map -> map.values.toList() }.flowOn(Dispatchers.IO)


    init {
        initScanner()
    }


    private fun initScanner(): Boolean {
        if (isInitialized) return run {
            emitError("Cannot init scanner: already initialized")
            false
        }
        blManager = context.getSystemService<BluetoothManager>()
        blAdapter = blManager?.adapter ?: return run {
            emitError("blManager system service not found")
            false
        }
        bleScanner = blAdapter?.bluetoothLeScanner ?: return run {
            emitError("blAdapter is null. Is bluetooth disabled on this device?")
            false
        }
        isInitialized = true
        return true
    }

    fun startScanForAllDevices(isScanStarted: (Boolean) -> Unit) {
        if (!isScanning) {
            scope.async {
                bleScanner?.startScan(
                    ScanConfig.defaultConfig.filters,
                    ScanConfig.defaultConfig.settings,
                    defaultScanCallback
                )
                isScanning = true
            }.invokeOnCompletion {
                isScanStarted(true)
            }
        } else {
            emitError("Can't scan for all devices: already scanning")
            isScanStarted(false)
        }
    }

    fun startScanForMyDevices(
        isScanStarted: (Boolean) -> Unit
    ) {
        if (!isScanning) {
            scope.async {
                bleScanner?.startScan(
                    ScanConfig.myDevicesConfig.filters,
                    ScanConfig.myDevicesConfig.settings,
                    defaultScanCallback
                )
                isScanning = true
            }.invokeOnCompletion {
                isScanStarted(true)
            }
        } else {
            emitError("Can't scan for my devices: already scanning")
            isScanStarted(false)
        }
    }

    fun stopScan(): Boolean {
        if (isScanning) {
            bleScanner?.stopScan(defaultScanCallback)
            isScanning = false
            return true
        } else {
            emitError("Can't stop scan: not scanning")
            return false
        }
    }


    // Will only deliver results if delay in ScanSettings is > 0
    // Probably not needed
    fun flushLastScans() {
        if (isScanning) bleScanner!!.flushPendingScanResults(defaultScanCallback)
        else emitError("Cannot flush scans while not scanning")
    }

    fun clearDevicesMap() {
        _scannedDevicesMap.update { emptyMap() }
    }

    private fun reset() {
        scope.cancelChildren()
        stopScan()
        isInitialized = false
    }

    private fun emitError(text: String) {
        scope.launch {
            _errors.emit(text)
        }
    }

}
