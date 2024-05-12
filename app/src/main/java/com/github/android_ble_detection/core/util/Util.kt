package com.github.android_ble_detection.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.cancellation.CancellationException

fun CoroutineScope.cancelChildren(cause: CancellationException? = null) = coroutineContext.cancelChildren(cause = cause)