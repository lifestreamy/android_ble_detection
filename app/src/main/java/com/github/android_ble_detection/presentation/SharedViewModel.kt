package com.github.android_ble_detection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.android_ble_detection.data.bluetooth.BluetoothLEController
import com.github.android_ble_detection.domain.model.BluetoothLeDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(private val blController: BluetoothLEController) :
    ViewModel() {

    val errors = blController.errors

    val uiState: StateFlow<SharedScreenState> = combine(
        blController.scannedDevicesListFlow,
        blController.flushedDevicesList
    ) { scanned, flushed ->
        SharedScreenState(
            scanned,
            flushed
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(1000),
        SharedScreenState()
    )




    fun startScan() = blController.startScan()

    fun stopScan() = blController.stopScan()


    fun startScanForDuration() = blController.startScanForDuration(10000)

    fun flushLastScans() = blController.flushLastScans()

}

data class SharedScreenState(
    val scannedDevices: List<BluetoothLeDevice> = emptyList(),
    val lastFlushedDevices: List<BluetoothLeDevice> = emptyList()
)

