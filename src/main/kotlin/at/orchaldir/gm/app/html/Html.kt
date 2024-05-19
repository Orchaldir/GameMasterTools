package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.TITLE
import kotlinx.html.*

fun HTML.simpleHtml(
    title: String,
    content: BODY.() -> Unit,
) {
    head {
        title { +TITLE }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
        script(src = "/static/scripts.js") {
            charset = "utf-8"
            defer = true
        }
    }
    body {
        h1 { +title }
        content()
    }
}

fun BODY.field(name: String, value: String) {
    p {
        b { +"$name: " }
        +value
    }
}

fun BODY.field(name: String, content: P.() -> Unit) {
    p {
        b { +"$name: " }
        content()
    }
}

fun BODY.fieldLink(label: String, link: String, text: String) {
    p {
        b { +"$label: " }
        a(link) { +text }
    }
}

// lists

fun <T> HtmlBlockTag.showList(
    elements: Collection<T>,
    content: LI.(T) -> Unit,
) {
    ul {
        elements.forEach { element ->
            li {
                content(element)
            }
        }
    }
}

fun <K, V> HtmlBlockTag.showMap(
    map: Map<K, V>,
    content: LI.(K, V) -> Unit,
) {
    ul {
        map.forEach { (key, value) ->
            li {
                content(key, value)
            }
        }
    }
}

// form

fun FORM.field(label: String, content: P.() -> Unit) {
    p {
        b { +"$label: " }
        content()
    }
}

fun <T> FORM.selectEnum(label: String, selectId: String, values: Collection<T>, content: OPTION.(T) -> Unit) {
    field(label) {
        select {
            id = selectId
            name = selectId
            values.forEach { gender ->
                option {
                    content(gender)
                }
            }
        }
    }
}
