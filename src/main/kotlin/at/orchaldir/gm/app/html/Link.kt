package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.app.plugins.character.Characters
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.moon.MoonId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.selector.getName
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.p

// generic

fun HtmlBlockTag.back(href: String) = action(href, "Back")

fun HtmlBlockTag.action(
    href: String,
    text: String,
) = p { link(href, text) }

fun HtmlBlockTag.link(
    href: String,
    text: String,
) = a(href) { +text }

// element

fun <ID : Id<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    link(call, id, state.getElementName(id))
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    element: ELEMENT,
) {
    link(call, element.id(), element.name())
}

fun <ID : Id<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    id: ID,
    text: String,
) = link(href(call, id), text)

fun <ID : Id<ID>> href(
    call: ApplicationCall,
    id: ID,
) = when (id) {
    is CalendarId -> call.application.href(Calendars.Details(id))
    is CharacterId -> call.application.href(Characters.Details(id))
    is CultureId -> call.application.href(Cultures.Details(id))
    is FashionId -> call.application.href(Fashions.Details(id))
    is ItemTemplateId -> call.application.href(ItemTemplates.Details(id))
    is LanguageId -> call.application.href(Languages.Details(id))
    is MaterialId -> call.application.href(Materials.Details(id))
    is MoonId -> call.application.href(Moons.Details(id))
    is NameListId -> call.application.href(NameLists.Details(id))
    is PersonalityTraitId -> call.application.href(Personality.Details(id))
    is RaceId -> call.application.href(Races.Details(id))
    else -> error("Cannot create link for unsupported type ${id.type()}!")
}

// character

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: CharacterId,
) {
    link(call, id, state.getName(id))
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    link(call, character.id, state.getName(character))
}
