package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme
import com.github.android_ble_detection.presentation.ui.util.ThemePreviews

@Composable
fun DeviceItem(
    modifier: Modifier = Modifier,
//    isAuthorized: Int? = null, to show a checkmark next to device later?
    name: String?,
    address: String?,
    signalStrength: Int,
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
                .padding(horizontal = 5.dp))
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = name ?: "Unknown name",
                fontSize = if (address != null) 16.sp else 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (address != null) {
                Text(
                    text = "Address: $address",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Spacer(Modifier.weight(1f))
        VerticalDivider(
            Modifier
                .fillMaxHeight(0.5f)
                .padding(horizontal = 5.dp))
        Text(text = "Signal strength:\n$signalStrength")
        VerticalDivider(
            Modifier
                .fillMaxHeight(0.5f)
                .padding(horizontal = 5.dp))
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
                onItemClick = {}
            )
        }
    }
}