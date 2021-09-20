package ru.impression.compose_jb_routing

import androidx.compose.runtime.*

var _routing: Routing? = null

val routing: Routing get() = _routing!!

fun initRouting(startLocation: String) {
    _routing = Routing(startLocation)
}

class Routing internal constructor(startLocation: String) {

    @PublishedApi
    internal var _location: String by mutableStateOf(startLocation)

    val location: String get() = _location

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
        _location = newLocation
    }

    fun pop(): Boolean = _history.removeLastOrNull()?.also { _location = it } != null
}

@Composable
inline fun Router(block: RouterScope.() -> Unit) {
    var routeBlock: (@Composable RouteScope.() -> Unit)? = null
    var routeScope: RouteScope? = null
    block(object : RouterScope() {
        override fun case(route: String, exact: Boolean, block: @Composable RouteScope.() -> Unit) {
            if (routeBlock != null) return
            val take: Boolean
            if (exact && !route.contains('{')) {
                take = routing._location == route
            } else {
                var routeRegex = route
                var indexOfParam = routeRegex.indexOf('{')
                while (indexOfParam != -1) {
                    val param = routeRegex.substring(indexOfParam..routeRegex.indexOf('}', indexOfParam))
                    routeRegex = routeRegex.replaceFirst(param, "[^/;]+")
                    indexOfParam = routeRegex.indexOf('{')
                }
                routeRegex += if (exact) "$" else ".*"
                take = routing._location.matches(routeRegex.toRegex())
            }
            if (take) {
                routeBlock = block
                val params = route.extractParams()
                routeScope = object : RouteScope() {
                    override fun param(name: String): String = params["{$name}"]!!
                }
            }
        }
    })
    routeBlock?.let { it(routeScope ?: return@let) }
}

abstract class RouterScope @PublishedApi internal constructor() {

    abstract fun case(route: String, exact: Boolean = false, block: @Composable RouteScope.() -> Unit)
}

abstract class RouteScope @PublishedApi internal constructor() {

    abstract fun param(name: String): String
}

@Composable
fun Redirect(location: String, vararg params: Pair<String, String>) {
    LaunchedEffect("Redirect") {
        routing.redirect(location, *params)
    }
}

@PublishedApi
internal fun String.extractParams(): Map<String, String> {
    val currentLocation = routing.location
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

fun String.query(): Map<String, String> =
    substringAfter('?', "").split('&').associate { it.substringBefore('=') to substringAfter('=') }