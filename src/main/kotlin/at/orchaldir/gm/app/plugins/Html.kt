package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.LanguageId
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

// link character

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: CharacterId,
) {
    link(call, id, state.characters.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    character: Character,
) {
    link(call, character.id, character.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: CharacterId,
    text: String
) {
    val characterLink = call.application.href(Characters.Details(Characters(), id))
    a(characterLink) { +text }
}

// link language

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: LanguageId,
) {
    link(call, id, state.languages.get(id)?.name ?: "Unknown")
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: LanguageId,
    text: String
) {
    val characterLink = call.application.href(Languages.Details(Languages(), id))
    a(characterLink) { +text }
}

// lists

fun HtmlBlockTag.characterList(
    call: ApplicationCall,
    characters: Collection<Character>,
) {
    ul {
        characters.forEach { character ->
            li {
                link(call, character)
            }
        }
    }
}

// form

fun FORM.label(name: String, content: P.() -> Unit) {
    p {
        b { +"$name: " }
        content()
    }
}