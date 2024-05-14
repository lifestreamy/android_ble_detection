package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme
import com.github.android_ble_detection.presentation.ui.util.ThemePreviews

@Composable
fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    defaultFontSize: TextUnit = 30.sp
) {
    var resizedTextStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        softWrap = false,
        style = resizedTextStyle,
        overflow = TextOverflow.Clip,
        onTextLayout = {
            if (it.didOverflowWidth) {
                if(style.fontSize.isUnspecified) resizedTextStyle.copy(fontSize = defaultFontSize)
                resizedTextStyle =
                    resizedTextStyle.copy(fontSize = resizedTextStyle.fontSize * 0.95)
            } else {
                readyToDraw = true
            }
        })

}

@ThemePreviews
@Composable
private fun ResizedTextPreview() {
    AndroidBleDetectionTheme {
        Column(Modifier.width(100.dp).background(color = Color.LightGray)) {
            AutoResizingText(
                text = "Sample textqwasdasdrqr",
                modifier = Modifier.height(30.dp)
            )
            AutoResizingText(
                text = "Sample textasdasda",
                modifier = Modifier.height(30.dp)
            )
            AutoResizingText(
                text = "Sample text",
                modifier = Modifier.height(60.dp)
            )
            AutoResizingText(
                text = "Sample text",
                modifier = Modifier.height(100.dp)
            )
        }
    }
}