package ru.impression.compose_jb_routing

val routing: BrowserRouting
    get() = (_routing ?: throw IllegalStateException("Call initRouting first")) as BrowserRouting

fun initRouting(startLocation: String) {
    _routing = BrowserRouting(startLocation)
}