@file:SuppressLint("MissingPermission")

package com.github.android_ble_detection.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.github.android_ble_detection.domain.model.BluetoothLeDevice

internal class DefaultScanCallback(
    /** For filtering devices and finding/removing ones from list based on criteria */
    private val singleDeviceScanCallback: (callbackType: SingleCallbackType, device: BluetoothLeDevice) -> Unit,
    private val onBatchFlush: (deviceList : List<BluetoothLeDevice>) -> Unit,
    private val onScanFailed: (scanException: ScanException) -> Unit
) : ScanCallback() {

    /**
     * Callback when a BLE advertisement has been found.
     * @param callbackType
     * Determines how this callback was triggered.
     * Could be one of
     * * ScanSettings.CALLBACK_TYPE_ALL_MATCHES
     * * ScanSettings.CALLBACK_TYPE_FIRST_MATCH
     * * ScanSettings.CALLBACK_TYPE_MATCH_LOST
     *
     * @param result A Bluetooth LE scan result
     * */
    override fun onScanResult(
        callbackType: Int,
        result: ScanResult
    ) {
        super.onScanResult(
            callbackType,
            result
        )
        val type = when (callbackType) {
            ScanSettings.CALLBACK_TYPE_ALL_MATCHES -> SingleCallbackType.ALL_MATCHES
            ScanSettings.CALLBACK_TYPE_FIRST_MATCH -> SingleCallbackType.FIRST_MATCH
            ScanSettings.CALLBACK_TYPE_MATCH_LOST -> SingleCallbackType.MATCH_LOST
            else -> SingleCallbackType.UNKNOWN
        }

        val device = BluetoothLeDevice(
            name = result.device.name,
            address = result.device.address,
            signalStrength = result.rssi
        )

        singleDeviceScanCallback(type, device)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        super.onBatchScanResults(results)
        onBatchFlush(
            results.map { scanResult ->
                BluetoothLeDevice(
                    name = scanResult.device.name,
                    address = scanResult.device.address,
                    signalStrength = scanResult.rssi
                )
            })

    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        val eMessage = "SCAN_FAILED_" + when (errorCode) {

            /**
             * Fails to start scan as BLE scan with the same settings is already started by the app.
             */
            SCAN_FAILED_ALREADY_STARTED -> "ALREADY_STARTED"

            /**
             * Fails to start scan as app cannot be registered.
             */
            SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "APPLICATION_REGISTRATION_FAILED"

            /**
             * Fails to start scan due an internal error
             */
            SCAN_FAILED_INTERNAL_ERROR -> "INTERNAL_ERROR "

            /**
             * Fails to start power optimized scan as this feature is not supported.
             */
            SCAN_FAILED_FEATURE_UNSUPPORTED -> "FEATURE_UNSUPPORTED"

            /**
             * Fails to start scan as it is out of hardware resources.
             */
            SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "OUT_OF_HARDWARE_RESOURCES"

            /**
             * Fails to start scan as application tries to scan too frequently.
             */
            SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> "SCANNING_TOO_FREQUENTLY"

            else -> { /* Nothing, when is exhaustive for the intDef */
            }
        }
        onScanFailed(ScanException(eMessage))
    }
}

enum class SingleCallbackType(val defaultCode: Int) {
    /**
     *      * Trigger a callback for every Bluetooth advertisement found that matches the filter criteria.
     *      * If no filter is active, all advertisement packets are reported.
     *      */
    ALL_MATCHES(1),

    /**
     *      * A result callback is only triggered for the first advertisement packet received that matches
     *      * the filter criteria.
     *      */
    FIRST_MATCH(2),

    /**
     *    * Receive a callback when advertisements are no longer received from a device that has been
     *    * previously reported by a first match callback.
     *    */
    MATCH_LOST(4),

    UNKNOWN(-1)
}

internal class ScanException(val msg: String = "ScanException") : Exception(msg)