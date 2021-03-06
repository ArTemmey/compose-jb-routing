package ru.impression.compose_jb_routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
inline fun Router(block: RouterScope.() -> Unit) {
    var routeBlock: (@Composable RouteScope.() -> Unit)? = null
    var routeScope: RouteScope? = null
    block(object : RouterScope() {
        override fun route(route: String, exact: Boolean, block: @Composable RouteScope.() -> Unit) {
            if (routeBlock != null) return
            if (_routing!!.location.matches(route, exact = exact)) {
                routeBlock = block
                val params = route.extractParams()
                routeScope = object : RouteScope() {
                    override fun param(name: String): String =
                        params[name] ?: throw Exception("No param with name $name")
                }
            }
        }
    })
    routeBlock?.let { routeBlock ->
        routeScope?.let { routeScope -> routeBlock(routeScope) }
    }
}

abstract class RouterScope @PublishedApi internal constructor() {

    abstract fun route(route: String, exact: Boolean = false, block: @Composable RouteScope.() -> Unit)
}

abstract class RouteScope @PublishedApi internal constructor() {

    abstract fun param(name: String): String
}

@Composable
fun Redirect(location: String, vararg params: Pair<String, String>) {
    LaunchedEffect(Unit) {
        _routing!!.redirect(location, *params)
    }
}