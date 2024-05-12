package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = ButtonDefaults.outlinedShape,
    onClick : () -> Unit
) {
    Button(modifier = modifier, shape = shape, onClick = onClick) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun DBPreview() {
    AndroidBleDetectionTheme {
        DefaultButton(text = "Text") { }
    }
}