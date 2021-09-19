import org.junit.Test
import ru.impression.compose_jb_routing.initRouting
import ru.impression.compose_jb_routing.routing

class AlgTest {

    @Test
    fun test() {
        initRouting("/projects/abc/cba/wef")
        routing.switch {
            case("/projects/{p1}/{p2}") {
                println("/projects" + param("p1") + param("p2"))
            }
        }
        println("DONE")
        // routing.push("/projects/{p}/{p1}/ui")
        // println(routing.location)
    }
}

object MyAppLocation {
    const val Home = "/"
    const val Login = "/login"
    const val Articles = "/articles"
    const val Article = "$Articles/{id}"
}

fun App() {
    initRouting(MyAppLocation.Home)
    routing.switch {
        case(MyAppLocation.Home, exact = true) { Home() }
        case(MyAppLocation.Articles, exact = true) { Articles() }
        case(MyAppLocation.Article) { Article(param("id")) }
    }
}

fun Home() {

}

fun Articles() {

}

fun Article(id: String) {

}