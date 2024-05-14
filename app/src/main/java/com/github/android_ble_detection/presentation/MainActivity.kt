package com.github.android_ble_detection.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import com.github.android_ble_detection.presentation.devices_screen.DevicesRoute
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme
import dagger.hilt.android.AndroidEntryPoint
import npo.kib.odc_demo.core.common.data.permissions.PermissionProvider

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AndroidBleDetectionTheme {
                CompositionLocalProvider(
                    PermissionProvider.LocalAppBluetoothPermissions provides PermissionProvider.bluetoothPermissionsList
                ) {
                        DevicesRoute(vm = sharedViewModel)
                }
            }
        }
    }
}