package ru.impression.compose_jb_routing

import kotlinx.browser.window

actual fun initRouting(startLocation: String) {
    _routing = Routing(startLocation)
}

actual class Routing internal constructor(startLocation: String) : BaseRouting(startLocation) {

    init {
        window.onpopstate = {
            refresh()
        }
    }

    override fun navigate(to: String, vararg params: Pair<String, String>, addToHistory: Boolean) {
        super.navigate(to, *params, addToHistory = addToHistory)
        if (addToHistory)
            window.history.pushState(null, "", to)
        else
            window.location.pathname = to
        refresh()
    }

    private fun refresh() {
        _location.value = Location(window.location.pathname)
    }

    override fun pop() {
        window.history.back()
    }
}