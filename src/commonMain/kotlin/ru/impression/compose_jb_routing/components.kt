package ru.impression.compose_jb_routing

@Composable
inline fun Router(block: RouterScope.() -> Unit) {
    var routeBlock: (@Composable RouteScope.() -> Unit)? = null
    var routeScope: RouteScope? = null
    block(object : RouterScope() {
        override fun route(route: String, exact: Boolean, block: @Composable RouteScope.() -> Unit) {
            if (routeBlock != null) return
            val take: Boolean
            if (exact && !route.contains('{')) {
                take = _routing!!._location.value == route
            } else {
                var routeRegex = route
                var indexOfParam = routeRegex.indexOf('{')
                while (indexOfParam != -1) {
                    val param = routeRegex.substring(indexOfParam..routeRegex.indexOf('}', indexOfParam))
                    routeRegex = routeRegex.replaceFirst(param, "[^/;]+")
                    indexOfParam = routeRegex.indexOf('{')
                }
                routeRegex += if (exact) "$" else ".*"
                take = _routing!!._location.value.matches(routeRegex.toRegex())
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