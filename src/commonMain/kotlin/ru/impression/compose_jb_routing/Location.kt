package ru.impression.compose_jb_routing

class Location(val path: String) {

    val query: Map<String, String>
        get() = path.substringAfter('?', "")
            .split('&')
            .associate { it.substringBefore('=') to it.substringAfter('=') }

    fun matches(route: String, vararg params: Pair<String, String>, exact: Boolean = false): Boolean {
        val resultRoute = route.setParams(*params)
        val result = if (exact && !resultRoute.contains('{')) {
            path == resultRoute
        } else {
            var routeRegex = resultRoute
            var indexOfParam = routeRegex.indexOf('{')
            while (indexOfParam != -1) {
                val param = routeRegex.substring(indexOfParam..routeRegex.indexOf('}', indexOfParam))
                routeRegex = routeRegex.replaceFirst(param, "[^/;]+")
                indexOfParam = routeRegex.indexOf('{')
            }
            routeRegex += if (exact) "$" else ".*"
            path.matches(routeRegex.toRegex())
        }
        return result
    }

    override fun toString() = path
}