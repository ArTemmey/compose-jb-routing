package ru.impression.compose_jb_routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

class Routing internal constructor(startLocation: String) {

    private var _location: String by mutableStateOf(startLocation)

    val location: String get() = _location

    private val _history = ArrayList<String>()

    val history get() = _history.toList()

    @Composable
    fun switch(block: SwitchScope.() -> Unit) {
        val switchScope = SwitchScope()
        block(switchScope)
        switchScope.cases.firstOrNull {
            if (it.exact && !it.route.contains('{'))
                return@firstOrNull _location == it.route
            var routeRegex = it.route
            var indexOfParam = routeRegex.indexOf('{')
            while (indexOfParam != -1) {
                val param = routeRegex.substring(indexOfParam..routeRegex.indexOf('}', indexOfParam))
                routeRegex = routeRegex.replaceFirst(param, "[^/;]+")
                indexOfParam = routeRegex.indexOf('{')
            }
            if (it.exact) routeRegex += "$" else routeRegex += ".*"
            _location.matches(routeRegex.toRegex())
        }?.let {
            val params = it.route.extractParams()
            it.block(object : CaseScope() {
                override fun param(name: String): String = params["{$name}"]!!
            })
        }
    }

    private fun String.extractParams(): Map<String, String> {
        val currentLocation = location
        var newLocation = this.substringBefore('?')
        val result = HashMap<String, String>()
        var indexOfParam = newLocation.indexOf("/{")
        while (indexOfParam != -1) {
            if (currentLocation.length <= indexOfParam) throw IllegalArgumentException("Param not set")
            val partOfNew = newLocation.substring(0..indexOfParam)
            val partOfCurrent = currentLocation.substring(0..indexOfParam)
            if (partOfCurrent != partOfNew) throw IllegalArgumentException("Param not set")
            val paramValue =
                currentLocation.substring(
                    indexOfParam + 1 until
                            (currentLocation.indexOf('/', indexOfParam + 1).takeIf { it != -1 }
                                ?: currentLocation.length)
                )
            val paramStub = newLocation.substring(indexOfParam + 1..newLocation.indexOf('}'))
            newLocation = newLocation.replaceFirst(paramStub, paramValue)
            result[paramStub] = paramValue
            indexOfParam = newLocation.indexOf("/{")
        }
        return result
    }

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
        _location = newLocation
    }

    fun pop(): Boolean =
        _history.removeLastOrNull()?.also { _location = it } != null
}

var _routing: Routing? = null

val routing: Routing get() = _routing!!

fun initRouting(startLocation: String) {
    _routing = Routing(startLocation)
}


internal class Case(val route: String, val exact: Boolean, val block: @Composable CaseScope.() -> Unit)

class SwitchScope internal constructor() {

    internal val cases = ArrayList<Case>()

    fun case(route: String, exact: Boolean = false, block: @Composable CaseScope.() -> Unit) {
        cases.add(Case(route, exact, block))
    }
}

abstract class CaseScope internal constructor() {

    abstract fun param(name: String): String
}

fun String.query(): Map<String, String> =
    substringAfter('?', "").split('&').associate { it.substringBefore('=') to substringAfter('=') }