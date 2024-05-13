package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.core.model.character.Character
import io.ktor.server.application.*
import io.ktor.server.resources.*
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

fun BODY.field(name: String, value: String) {
    p {
        b { +"$name: " }
        +value
    }
}

fun BODY.fieldLink(label: String, link: String, text: String) {
    p {
        b { +"$label: " }
        a(link) { +text }
    }
}

// lists

fun HtmlBlockTag.characterList(
    call: ApplicationCall,
    characters: Collection<Character>,
) {
    ul {
        characters.forEach { character ->
            li {
                val characterLink = call.application.href(Characters.Details(Characters(), character.id))
                a(characterLink) { +character.name }
            }
        }
    }
}