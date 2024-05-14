package com.github.android_ble_detection.data.bluetooth

import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import com.github.android_ble_detection.data.bluetooth.BleConstants.myXiaomiMac

object ScanConfig {

    val defaultConfig = BleScanConfigType.AllDevices

    val myDevicesConfig = BleScanConfigType.SelectedMacDevicesOnly

    sealed class BleScanConfigType(
        val filters: List<ScanFilter>,
        val settings: ScanSettings
    ) {
        data object AllDevices : BleScanConfigType(
            filters = emptyList(),
            settings = BleScanSettings.defaultScanSettings
        )

        data object SelectedMacDevicesOnly : BleScanConfigType(
            filters = BleScanFilters.mySmartWatchScanFilterAsList,
            settings = BleScanSettings.defaultScanSettings
        )
    }

}

private object BleScanSettings {

    val defaultScanSettings: ScanSettings = ScanSettings.Builder().apply {
        setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) //scans indefinitely, stop manually to conserve power
        setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) //callback will trigger on each received advertisement packet
//        setMatchMode(MATCH_MODE_AGGRESSIVE) // more sensitive
        setMatchMode(ScanSettings.MATCH_MODE_STICKY) //more packets and better signal strength needed
        setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT) // one packet needed for match
//        setNumOfMatches(MATCH_NUM_FEW_ADVERTISEMENT) // several packets needed for match
        // > 0 not needed
        setReportDelay(0L) // With == 0 the onScanResult() always triggers, with > 0 the onBatchScanResults() always triggers
    }.build()

}

private object BleScanFilters {


    private val selectedMacAddressesScanFilter: ScanFilter = ScanFilter.Builder().apply {
        setDeviceAddress(myXiaomiMac)
    }.build()

    //pass the list to startScan(List<ScanFilter>, ScanSettings, ScanCallback)
    val mySmartWatchScanFilterAsList: List<ScanFilter> = listOf(selectedMacAddressesScanFilter)

}

object BleConstants {

    const val myXiaomiMac = "E1:99:A2:FE:70:47"
    const val defaultThresholdDistance = 4 //Distance in cm at which the device is registered as being close

//        private const val myXiaomiName = "Xiaomi Smart Band 7 7047"

}