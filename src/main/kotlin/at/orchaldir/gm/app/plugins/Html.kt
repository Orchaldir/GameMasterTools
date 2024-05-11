package at.orchaldir.gm.app.plugins

import kotlinx.html.*

fun HTML.simpleHtml(
    title: String,
    content: BODY.() -> Unit
) {
    head {
        title { +TITLE }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
    }
    body {
        h1 { +title }
        content()
    }
}