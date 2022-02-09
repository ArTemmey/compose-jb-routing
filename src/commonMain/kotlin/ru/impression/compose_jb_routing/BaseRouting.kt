package ru.impression.compose_jb_routing

import androidx.compose.runtime.mutableStateOf

abstract class BaseRouting internal constructor(startPath: String) {

    @PublishedApi
    internal val _location = mutableStateOf(Location(startPath))

    val location: Location get() = _location.value

    fun push(path: String, vararg params: Pair<String, String>) {
        navigate(path, *params, addToHistory = true)
    }

    fun redirect(path: String, vararg params: Pair<String, String>) {
        navigate(path, *params, addToHistory = false)
    }

    protected open fun navigate(to: String, vararg params: Pair<String, String>, addToHistory: Boolean) {
        var newPath = to.setParams(*params)
        newPath = newPath.setParams(*newPath.extractParams().toList().toTypedArray())
        _location.value = Location(newPath)
    }

    abstract fun pop()
}
