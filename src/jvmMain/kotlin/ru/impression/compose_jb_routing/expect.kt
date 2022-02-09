package ru.impression.compose_jb_routing


actual fun initRouting(startLocation: String) {
    _routing = DesktopRouting(startLocation)
}