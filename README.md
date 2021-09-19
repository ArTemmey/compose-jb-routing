Easiest routing for compose-jb

## Installation

1. Clone repo
2. Run `./gradlew assemble` in it
3. Copy `build/libs` folder to your project
4. Add `implementation(files("../libs/compose-jb-routing-jvm-1.0-SNAPSHOT.jar"))` to your project's build.gradle

## Usage

1. Declare your app locations:

```kotlin
object MyAppLocation {
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
    initRouting(MyAppLocation.Home)
    routing.switch {
        case(MyAppLocation.Home, exact = true) { Home() }
        case(MyAppLocation.Articles, exact = true) { Articles() }
        // Obtaining {id} param
        case(MyAppLocation.Article) { Article(param("id")) }
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
            routing.push(MyAppLocation.Article, "id" to it.id)

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
fun PrivateLocation() {
    if (!auth)
    // Redirecting to login and setting current location as ref
        routing.redirect("${MyAppLocation.Login}?ref=${routing.location}")
}

@Composable
fun Login() {
    if (login()) {
        // If have ref - redirecting back to it, else - just navigate back
        routing.location.query()["ref"]
            ?.let { routing.redirect(it) }
            ?: routing.pop()
    }
}
```