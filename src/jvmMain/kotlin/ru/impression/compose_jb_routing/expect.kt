package ru.impression.compose_jb_routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope

actual typealias Composable = Composable

actual typealias MutableState<T> = MutableState<T>

actual fun <T> mutableStateOf(value: T) = androidx.compose.runtime.mutableStateOf(value)

@Composable
actual fun LaunchedEffect(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) = androidx.compose.runtime.LaunchedEffect(key1, block)