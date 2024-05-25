package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.app.plugins.character.Characters
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
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: CharacterId,
) = call.application.href(Characters.Details(id))

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
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: CultureId,
) = call.application.href(Cultures.Details(id))

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
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: LanguageId,
) = call.application.href(Languages.Details(id))

// personality

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: PersonalityTraitId,
) {
    link(call, id, state.personalityTraits.get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    trait: PersonalityTrait,
) {
    link(call, trait.id, trait.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: PersonalityTraitId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: PersonalityTraitId,
) = call.application.href(Personality.Details(id))

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
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: RaceId,
) = call.application.href(Races.Details(id))
