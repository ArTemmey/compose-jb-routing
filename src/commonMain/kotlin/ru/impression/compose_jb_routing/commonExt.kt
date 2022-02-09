package ru.impression.compose_jb_routing


@PublishedApi
internal var _routing: Routing? = null

val routing: Routing
    get() = _routing ?: throw IllegalStateException("Call initRouting first")

@PublishedApi
internal fun String.setParams(vararg params: Pair<String, String>): String {
    var result = this
    params.forEach {
        result = result.replace("{${it.first}}", it.second)
    }
    return result
}

@PublishedApi
internal fun String.extractParams(): Map<String, String> {
    val currentPath = _routing!!.location.path
    var newLocation = this.substringBefore('?')
    val result = HashMap<String, String>()
    var indexOfParam = newLocation.indexOf("/{")
    while (indexOfParam != -1) {
        if (currentPath.length <= indexOfParam) throw IllegalArgumentException("Param not set")
        val partOfNew = newLocation.substring(0..indexOfParam)
        val partOfCurrent = currentPath.substring(0..indexOfParam)
        if (partOfCurrent != partOfNew) throw IllegalArgumentException("Param not set")
        val paramValue =
            currentPath.substring(
                indexOfParam + 1 until
                        (currentPath.indexOf('/', indexOfParam + 1).takeIf { it != -1 }
                            ?: currentPath.length)
            )
        val paramName = newLocation.substring(indexOfParam + 2 until newLocation.indexOf('}'))
        newLocation = newLocation.replaceFirst("{$paramName}", paramValue)
        result[paramName] = paramValue
        indexOfParam = newLocation.indexOf("/{")
    }
    return result
}

fun String.query(): Map<String, String> =
    substringAfter('?', "")
        .split('&')
        .associate { it.substringBefore('=') to it.substringAfter('=') }