package ru.impression.compose_jb_routing


val routing: DesktopRouting
    get() = (_routing ?: throw IllegalStateException("Call initRouting first")) as DesktopRouting
