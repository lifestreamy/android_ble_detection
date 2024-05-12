package com.github.android_ble_detection.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    actionTextColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.primary
) {
    val visuals = snackbarData.visuals

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = surfaceColor,
        modifier = modifier.padding(5.dp),
        border = BorderStroke(5.dp, color = borderColor)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = visuals.message,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            visuals.actionLabel?.let {
                Column(modifier = Modifier.weight(1f)) {
                    TextButton(
                        onClick = { snackbarData.performAction() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)

                    ) {
                        Text(
                            text = it.uppercase(Locale.ROOT), color = actionTextColor,
                        )
                    }
                }
            }
        }
    }
}