package ru.impression.compose_jb_routing


abstract class Routing internal constructor(startLocation: String) {

    @PublishedApi
    internal val _location: MutableState<String> = mutableStateOf(startLocation)

    val location: String get() = _location.value

    fun push(location: String, vararg params: Pair<String, String>) {
        navigate(location, *params, addToHistory = true)
    }

    fun redirect(location: String, vararg params: Pair<String, String>) {
        navigate(location, *params, addToHistory = false)
    }

    protected open fun navigate(to: String, vararg params: Pair<String, String>, addToHistory: Boolean) {
        var newLocation = to
        params.forEach {
            newLocation = newLocation.replace("{${it.first}}", it.second)
        }
        newLocation.extractParams().forEach {
            newLocation = newLocation.replaceFirst(it.key, it.value)
        }
        _location.value = newLocation
    }

    abstract fun pop()
}
