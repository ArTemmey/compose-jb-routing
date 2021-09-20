package ru.impression.compose_jb_routing


class Routing internal constructor(startLocation: String) {

    @PublishedApi
    internal val _location: MutableState<String> = mutableStateOf(startLocation)

    val location: String get() = _location.value

    private val _history = ArrayList<String>()

    val history get() = _history.toList()

    fun push(location: String, vararg params: Pair<String, String>) {
        navigate(location, *params, addToHistory = true)
    }

    fun redirect(location: String, vararg params: Pair<String, String>) {
        navigate(location, *params, addToHistory = false)
    }

    private fun navigate(to: String, vararg params: Pair<String, String>, addToHistory: Boolean) {
        var newLocation = to
        params.forEach {
            newLocation = newLocation.replace("{${it.first}}", it.second)
        }
        newLocation.extractParams().forEach {
            newLocation = newLocation.replaceFirst(it.key, it.value)
        }
        if (addToHistory) _history += location
        _location.value = newLocation
    }

    fun pop(): Boolean = _history.removeLastOrNull()?.also { _location.value = it } != null
}
