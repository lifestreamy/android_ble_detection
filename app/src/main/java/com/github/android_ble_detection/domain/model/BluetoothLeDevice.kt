package com.github.android_ble_detection.domain.model

data class BluetoothLeDevice(
    val name: String? = null,
    val address: String = "",
    /**
     * The received signal strength in dBm. The valid range is [-127, 126].
     * */
    val signalStrength: Int = 0,
    /**
     * Estimated by applying a KalmanFilter
     * */
    val estimatedDistance : Int? = null
    )
