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

// character

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
    text: String,
) {
    val characterLink = call.application.href(Characters.Details(Characters(), id))
    a(characterLink) { +text }
}

// language

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: LanguageId,
) {
    link(call, id, state.languages.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    language: Language,
) {
    link(call, language.id, language.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: LanguageId,
    text: String,
) {
    val characterLink = call.application.href(Languages.Details(Languages(), id))
    a(characterLink) { +text }
}
