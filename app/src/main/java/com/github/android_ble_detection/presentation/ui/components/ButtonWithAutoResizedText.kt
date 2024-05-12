package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme

@Composable
fun ButtonWithAutoResizedText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    shape: Shape = RoundedCornerShape(5.dp),
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = shape,
        onClick = onClick
    ) {
        AutoResizingText(
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun AutoResTextButPrev() {
    AndroidBleDetectionTheme {
        ButtonWithAutoResizedText(text = "Text") { }
    }
}
