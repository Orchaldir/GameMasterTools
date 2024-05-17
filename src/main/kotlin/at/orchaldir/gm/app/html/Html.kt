package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.Characters
import at.orchaldir.gm.app.plugins.Languages
import at.orchaldir.gm.app.plugins.TITLE
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import io.ktor.server.resources.*
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

fun <ID : Id<ID>, E : Element<ID>> HtmlBlockTag.listElements(
    elements: Collection<E>,
    content: LI.(E) -> Unit,
) {
    ul {
        elements.forEach { element ->
            li {
                content(element)
            }
        }
    }
}

// form

fun FORM.label(label: String, content: P.() -> Unit) {
    p {
        b { +"$label: " }
        content()
    }
}

fun <T> FORM.selectEnum(label: String, selectId: String, values: Collection<T>, content: OPTION.(T) -> Unit) {
    label(label) {
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
