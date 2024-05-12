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


    private var isInitialized: Boolean = false
    private var isScanning: Boolean = false


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
                    SingleCallbackType.FIRST_MATCH -> { /* triggers when found device passes filter */
                    }
                    SingleCallbackType.MATCH_LOST -> {
                        _scannedDevicesMap.update { map ->
                            map.toMutableMap().apply { remove(device.address) }

                        }
                    }
                    SingleCallbackType.UNKNOWN -> {/* for when statement exhaustion, should not occur*/
                        throw Exception("SingleCallbackType.UNKNOWN found")
                    }
                }
            }
        },
            onBatchFlush = { list ->
                _flushedDevicesList.update { list }
            },
            onScanFailed = { scanExc ->
                scope.launch {
                    _errors.emit(scanExc.msg)
                }
            })


    private val _flushedDevicesList: MutableStateFlow<List<BluetoothLeDevice>> =
        MutableStateFlow(emptyList())
    val flushedDevicesList = _flushedDevicesList.asStateFlow()


    private val _scannedDevicesMap: MutableStateFlow<Map<String, BluetoothLeDevice>> =
        MutableStateFlow(emptyMap())
    val scannedDevicesListFlow : Flow<List<BluetoothLeDevice>> = _scannedDevicesMap.map { map -> map.values.toList() }.flowOn(Dispatchers.IO)


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

    fun startScan() {
//        if (!isScanning) {} commented out for test
        bleScanner?.startScan(defaultScanCallback)
        isScanning = true
    }

    fun stopScan() {
        if (isScanning) {
            bleScanner?.stopScan(defaultScanCallback)
            isScanning = false
        } else emitError("Can't stop scan: not scanning")
    }

    fun startScanForDuration(duration: Long) {
        scope.launch {
            if (!isInitialized) initScanner()
            if (!isScanning) {
                startScan()
                delay(duration)
                stopScan()
            } else _errors.emit("Can't start scan for duration: scan already started")
        }
    }


    fun flushLastScans() {
        if (isScanning) bleScanner!!.flushPendingScanResults(defaultScanCallback)
        else emitError("Cannot flush scans while not scanning")
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
