package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.android_ble_detection.domain.model.BluetoothLeDevice
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme
import com.github.android_ble_detection.presentation.ui.util.ThemePreviews

@Composable
fun DeviceItem(
    modifier: Modifier = Modifier,
    device: BluetoothLeDevice,
    onItemClick: () -> Unit
) {
    DeviceItem(
        modifier = modifier,
        name = device.name,
        address = device.address,
        signalStrength = device.signalStrength,
        estimatedDistance = device.estimatedDistance,
        onItemClick = onItemClick
    )
}

@Composable
fun DeviceItem(
    modifier: Modifier = Modifier,
    name: String?,
    address: String?,
    signalStrength: Int,
    estimatedDistance: Int?,
    onItemClick: () -> Unit
) {
        Row(
            modifier = modifier
                .requiredHeight(80.dp)
                .clickable { onItemClick() },
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            VerticalDivider(
                Modifier
                    .fillMaxHeight(0.7f)
                    .padding(horizontal = 1.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                AutoResizingText(
                    text = name ?: "Unknown name",
                    defaultFontSize = if (address != null) 16.sp else 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (address != null) {
                    AutoResizingText(
                        text = "Address: $address",
                        defaultFontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            VerticalDivider(
                Modifier
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 1.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                AutoResizingText(
                    text = "rssi: $signalStrength",
                    defaultFontSize = if (estimatedDistance != null) 10.sp else 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (estimatedDistance != null) {
                    AutoResizingText(
                        text = "Distance: $estimatedDistance cm",
                        defaultFontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            VerticalDivider(
                Modifier
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 1.dp)
            )
        }
}


@ThemePreviews
@Composable
private fun DeviceItemPreview() {
    AndroidBleDetectionTheme {
        Column {
            DeviceItem(
                name = "Sample name",
                address = "00:00:00:00:00:00",
                signalStrength = 505,
                estimatedDistance = 10,
                onItemClick = {}
            )
        }
    }
}