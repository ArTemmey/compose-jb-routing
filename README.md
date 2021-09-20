Easiest routing for compose-jb

## Installation

1. Clone repo
2. Run `./gradlew assemble` in it
3. Copy `build/libs` folder to your project
4. Add dependency  to your project's build.gradle
- For desktop
```kotlin
implementation(files("libs/compose-jb-routing-1.0-SNAPSHOT.jar"))
implementation(files("libs/compose-jb-routing-jvm-1.0-SNAPSHOT.jar"))
```
- For browser
```kotlin
implementation(files("libs/compose-jb-routing-1.0-SNAPSHOT.jar"))
implementation(files("libs/compose-jb-routing-js-1.0-SNAPSHOT.klib"))
```

## Usage

1. Declare your app routes:

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
    Router {
        // `exact = true` means that current location matches exactly the given route.
        route(MyAppRoute.Home, exact = true) { Home() }
        // Default value of `exact` (false) means that current location can be any of those that starts with the given route. In this example location matches "/articles.*"
        route(MyAppRoute.Articles) { Articles() }
    }
}

@Composable
fun Articles() {
    // Nested routing
    Router {
        route(MyAppRoute.Articles, exact = true) { ArticleList() }
        // Obtaining {id} param
        route(MyAppRoute.Article) { Article(param("id")) }
    }
}
```

3. Perform routing

```kotlin
@Composable
fun ArticleList() {
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
