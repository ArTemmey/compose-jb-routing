Easiest routing for compose-jb

## Installation

1. Clone repo
2. Run `./gradlew assemble` in it
3. Copy `build/libs` folder to your project
4. Add `implementation(files("../libs/compose-jb-routing-jvm-1.0-SNAPSHOT.jar"))` to your project's build.gradle

## Usage

1. Declare your app router:

```kotlin
object MyAppRoute {
    const val Home = "/"
    const val Articles = "/articles"

    // {id} is route param
    const val Article = "$Articles/{id}"
}
```

2. Configure routing

```kotlin
@Composable
fun App() {
    initRouting(MyAppRoute.Home)
    routing.switch {
        case(MyAppRoute.Home, exact = true) { Home() }
        case(MyAppRoute.Articles, exact = true) { Articles() }
        // Obtaining {id} param
        case(MyAppRoute.Article) { Article(param("id")) }
    }
}
```

3. Perform routing

```kotlin
@Composable
fun Articles() {
    articles.forEach {
        ArticlePreview(onClick = {
            // Navigate to next location and add current to the back stack
            routing.push(MyAppRoute.Article, "id" to it.id)

            // Here we can see the back stack
            println(routing.history)
        })
    }
}

@Composable
fun Article(id: String) {
    BackButton(onClick = {
        // Navigate to prev location
        routing.pop()
    })
}
```

4. Make redirects

```kotlin
@Composable
fun PrivateRoute() {
    if (!auth) {
        // Redirecting to login and setting current location as ref
        Redirect("${MyAppRoute.Login}?ref=${routing.location}")
        return
    }
}

@Composable
fun Login() {
    Button(onClick = {
        performLogin()
        // If have ref - redirecting back to it, else - just navigate back
        routing.location.query()["ref"]
            ?.let { routing.redirect(it) }
            ?: routing.pop()
    }) { Text("Login button") }
}
```