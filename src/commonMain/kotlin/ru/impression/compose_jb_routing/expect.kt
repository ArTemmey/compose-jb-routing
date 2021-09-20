package ru.impression.compose_jb_routing

import kotlinx.coroutines.CoroutineScope

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
expect annotation class Composable()

expect interface MutableState<T> {
    var value: T
}

expect fun <T> mutableStateOf(value: T): MutableState<T>

@Composable
expect fun LaunchedEffect(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
)