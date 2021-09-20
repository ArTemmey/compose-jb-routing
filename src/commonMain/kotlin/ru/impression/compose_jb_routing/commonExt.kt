package ru.impression.compose_jb_routing


@PublishedApi
internal var _routing: Routing? = null

@PublishedApi
internal fun String.extractParams(): Map<String, String> {
    val currentLocation = _routing!!.location
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
    substringAfter('?', "")
        .split('&')
        .associate { it.substringBefore('=') to substringAfter('=') }