package ru.impression.compose_jb_routing

class DesktopRouting internal constructor(startLocation: String) : Routing(startLocation) {

    private val _history = ArrayList<String>()

    val history get() = _history.toList()

    override fun navigate(to: String, vararg params: Pair<String, String>, addToHistory: Boolean) {
        if (addToHistory)
            _history += location
        super.navigate(to, *params, addToHistory = addToHistory)
    }

    override fun pop() {
        _history.removeLastOrNull()?.also { _location.value = it }
    }
}