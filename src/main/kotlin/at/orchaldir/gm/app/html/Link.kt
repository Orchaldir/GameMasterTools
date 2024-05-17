package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.Characters
import at.orchaldir.gm.app.plugins.Cultures
import at.orchaldir.gm.app.plugins.Languages
import at.orchaldir.gm.app.plugins.Races
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a

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

// culture

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: CultureId,
) {
    link(call, id, state.cultures.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    culture: Culture,
) {
    link(call, culture.id, culture.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: CultureId,
    text: String,
) {
    val characterLink = call.application.href(Cultures.Details(Cultures(), id))
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

// race

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: RaceId,
) {
    link(call, id, state.races.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    race: Race,
) {
    link(call, race.id, race.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: RaceId,
    text: String,
) {
    val characterLink = call.application.href(Races.Details(Races(), id))
    a(characterLink) { +text }
}
