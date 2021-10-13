package ru.impression.compose_jb_routing

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
    routeBlock?.let { it(routeScope ?: return@let) }
}

abstract class RouterScope @PublishedApi internal constructor() {

    abstract fun route(route: String, exact: Boolean = false, block: @Composable RouteScope.() -> Unit)
}

abstract class RouteScope @PublishedApi internal constructor() {

    abstract fun param(name: String): String
}

@Composable
fun Redirect(location: String, vararg params: Pair<String, String>) {
    LaunchedEffect("Redirect") {
        _routing!!.redirect(location, *params)
    }
}