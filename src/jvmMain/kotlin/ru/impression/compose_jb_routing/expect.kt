package ru.impression.compose_jb_routing


actual fun initRouting(startLocation: String) {
    _routing = Routing(startLocation)
}

actual class Routing internal constructor(startLocation: String) : BaseRouting(startLocation) {

    private val _history = ArrayList<Location>()

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