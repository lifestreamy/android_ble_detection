package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultiplePermissionsRequestBlock(
    modifier: Modifier = Modifier,
    permissionsRequestText: String,
    onRequestPermissionsClick: () -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = permissionsRequestText)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRequestPermissionsClick() } ){
            Text(text = "Request permissions")
        }
    }
}