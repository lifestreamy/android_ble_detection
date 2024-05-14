package com.github.android_ble_detection.presentation.devices_screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.android_ble_detection.core.permissions.getTextToShowGivenPermissions
import com.github.android_ble_detection.domain.model.BluetoothLeDevice
import com.github.android_ble_detection.presentation.SharedScreenEvent
import com.github.android_ble_detection.presentation.SharedScreenState
import com.github.android_ble_detection.presentation.SharedViewModel
import com.github.android_ble_detection.presentation.ui.components.*
import com.github.android_ble_detection.presentation.ui.theme.AndroidBleDetectionTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.common.data.permissions.PermissionProvider

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DevicesRoute(vm: SharedViewModel) {
    val insets = WindowInsets.safeDrawing
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .statusBarsPadding()
//            .navigationBarsPadding()
            .windowInsetsPadding(insets),
        contentAlignment = Alignment.TopCenter
//            .padding(insets.asPaddingValues())
//            .windowInsetsPadding(WindowInsets.safeDrawing)
//            .consumeWindowInsets(insets.asPaddingValues())
    ) {
        val multiplePermissionsState =
            rememberMultiplePermissionsState(permissions = PermissionProvider.LocalAppBluetoothPermissions.current)
        if (multiplePermissionsState.allPermissionsGranted) {
            val screenState by vm.screenState.collectAsStateWithLifecycle()

            DevicesScreen(
                screenState = screenState,
                snackbarNotifications = vm.snackbarNotifications,
                onEvent = vm::onEvent
            )
        } else {
            MultiplePermissionsRequestBlock(permissionsRequestText = getTextToShowGivenPermissions(
                multiplePermissionsState.revokedPermissions,
                multiplePermissionsState.shouldShowRationale,
            ),
                onRequestPermissionsClick = { multiplePermissionsState.launchMultiplePermissionRequest() })
        }
    }
}

@Composable
private fun DevicesScreen(
    screenState: SharedScreenState,
    snackbarNotifications: Flow<String>,
    onEvent: (SharedScreenEvent) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val boxWithConstraintsScope = this
        val bottomButtonMaxHeight = 50.dp
        val bottomButtonsVerticalSpacing = 5.dp
        val buttonRowVerticalPadding = 5.dp
        val bottomButtonRowMaxHeight =
            bottomButtonMaxHeight * 2 + bottomButtonsVerticalSpacing + buttonRowVerticalPadding * 2
        val topColumnMaxHeight = boxWithConstraintsScope.maxHeight - bottomButtonRowMaxHeight
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .heightIn(max = topColumnMaxHeight - 15.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 5.dp)
                )

                Text(text = "Scan for BLE devices")

                HorizontalDivider(
                    Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 5.dp)
                )

                LazyColumn(
                    Modifier.animateContentSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(items = screenState.scannedDevices) { device ->
                        DeviceItem(modifier = Modifier.fillMaxWidth(),
                            name = device.name,
                            address = device.address,
                            signalStrength = device.signalStrength,
                            estimatedDistance = device.estimatedDistance,
                            onItemClick = { })
                    }
                }

                HorizontalDivider(
                    Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 10.dp)
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .requiredHeightIn(
                        max = bottomButtonRowMaxHeight
                    )
                    .padding(
                        horizontal = 5.dp,
                        vertical = buttonRowVerticalPadding
                    ),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(bottomButtonsVerticalSpacing)
                ) {
                    ButtonWithAutoResizedText(text = "Scan for all devices",
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = bottomButtonMaxHeight)
                            .aspectRatio(4f),
                        onClick = { onEvent(SharedScreenEvent.StartScanForAllDevices) })
                    ButtonWithAutoResizedText(text = "Stop scan",
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = bottomButtonMaxHeight)
                            .aspectRatio(4f),
                        onClick = { onEvent(SharedScreenEvent.StopScan) })
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(bottomButtonsVerticalSpacing)
                ) {
                    ButtonWithAutoResizedText(text = "Detect my devices",
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = bottomButtonMaxHeight)
                            .aspectRatio(4f),
                        onClick = { onEvent(SharedScreenEvent.StartScanForMyDevices) })
                    ButtonWithAutoResizedText(text = "Clear list",
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = bottomButtonMaxHeight)
                            .aspectRatio(4f),
                        onClick = { onEvent(SharedScreenEvent.ClearDevicesList) })
                }
            }
        }

        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(key1 = Unit) {
            snackbarNotifications.collect { message ->
                val job = launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Indefinite
                    )
                }
                delay(1000)
                job.cancel()
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .requiredHeightIn(
                    min = 80.dp,
                    max = 300.dp
                )
                .offset(y = -bottomButtonRowMaxHeight)
                .align(Alignment.BottomCenter)
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData,
                modifier = Modifier.padding(horizontal = 15.dp),
                textColor = Color.White.copy(alpha = 0.8f),
                surfaceColor = Color.DarkGray.copy(alpha = 0.5f),
                borderColor = Color.Transparent
            )
        }
    }
}

@Preview
@Composable
private fun DevicesScreenPreview() {
    AndroidBleDetectionTheme {
        DevicesScreen(screenState = SharedScreenState(buildList {
            repeat(15) { this.add(BluetoothLeDevice()) }
        }),
            snackbarNotifications = flowOf("a", "b", "c", "d"),
            onEvent = {} )
    }
}