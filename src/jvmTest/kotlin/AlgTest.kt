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