package com.github.android_ble_detection.data.bluetooth

import java.io.Serializable
import kotlin.math.pow

internal class KalmanFilter(
    private val R: Double,      //  Process Noise . Looks like a constant delta
    private val Q: Double,      //  Measurement Noise
    private val A: Double = 1.0,//  State Vector
    private val B: Double = 0.0,//  Control Vector
    private val C: Double = 1.0 //  Measurement Vector
) : Serializable {
    companion object {
        // Default Kalman R (Process Noise) & Q (Measurement Noise)
        private const val DEFAULT_KALMAN_R = 0.06 /*0.125*/ //some reference
        private const val DEFAULT_KALMAN_Q = 0.4 /*0.5*/    //values fo ble

        val default: KalmanFilter
            get() = KalmanFilter(
                DEFAULT_KALMAN_R,
                DEFAULT_KALMAN_Q
            )
    }

    private var filteredValue: Double? = null // Resulting Filtered Measurement Value (No Noise)
    private var covariance: Double = 0.0 //  Covariance

    private fun predictValue(control: Double): Double = (A * filteredValue!!) + (B * control)

    private val uncertainty: Double
        get() = A.pow(2) * covariance + R


    /**
     * Filters a measurement (rssi)
     *
     * @param measurement The measurement value to be filtered (rssi)
     * @param u The controlled input value
     * @return The filtered value
     */
    fun applyFilter(
        measurement: Double,
        u: Double = 0.0
    ): Double {
        val predictedValue: Double //  Predicted Measurement Value
        val kalmanGain: Double //  Kalman Gain
        val predictedCov: Double //  Predicted Covariance
        if (filteredValue == null) {
            filteredValue = measurement / C
            covariance = Q / C.pow(2)
        } else {
            predictedValue = predictValue(u)
            predictedCov = uncertainty
            kalmanGain = (C + Q / (predictedCov * C)).pow(-1)
            filteredValue = predictedValue + kalmanGain * (measurement - C * predictedValue)
            covariance = predictedCov * (1 - kalmanGain * C)
        }
        return filteredValue!!
    }

    override fun toString(): String = "KalmanFilter{R=$R, Q=$Q, A=$A, B=$B, C=$C, x=$filteredValue, cov=$covariance}"

}


fun Double.calculateDistanceFromRssi(
    txPower: Int = 55 //signal power at 1 m for 2 specific devices
): Double {
    if (this == 0.0) return -1.0 // if we cannot determine accuracy, return -1.

    val ratio = this * 1.0 / txPower
// L = l0 * 10 ^ ((p0 - p) / 10 * n /* q для воздуха 2, с препятствиями > 2*/ ) //
    return if (ratio < 1.0) ratio.pow(10.0) + 0.01 else (0.8) * ratio.pow(7.0) + 0.01
//        val accuracy = (0.89976) * ratio.pow(7.7095) + 0.111  old reference values
}