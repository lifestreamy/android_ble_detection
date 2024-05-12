package com.github.android_ble_detection.data.bluetooth

import android.bluetooth.le.ScanFilter


val defaultScanFilter = ScanFilter.Builder().apply {
}

//pass the list to startScan(List<ScanFilter>, ScanSettings, ScanCallback)
val bleScanFilters : List<ScanFilter> by lazy { listOf()}