package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.app.plugins.character.Characters
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CALENDAR
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.CULTURE
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FASHION
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.item.ITEM_TEMPLATE
import at.orchaldir.gm.core.model.item.ItemTemplate
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.LANGUAGE
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.Material
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.selector.getName
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a

// generic

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
) = a(href(call, id)) { +text }

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

// material

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: MaterialId,
) {
    link(call, id, state.getMaterialStorage().get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    material: Material,
) {
    link(call, material.id, material.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: MaterialId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: MaterialId,
) = call.application.href(Materials.Details(id))

// name list

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: NameListId,
) {
    link(call, id, state.getNameListStorage().get(id)?.name ?: "Unknown")
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    nameList: NameList,
) {
    link(call, nameList.id, nameList.name)
}

private fun HtmlBlockTag.link(
    call: ApplicationCall,
    id: NameListId,
    text: String,
) = a(href(call, id)) { +text }

fun href(
    call: ApplicationCall,
    id: NameListId,
) = call.application.href(NameLists.Details(id))

// personality

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: PersonalityTraitId,
) {
    link(call, id, state.getPersonalityTraitStorage().get(id)?.name ?: "Unknown")
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
    link(call, id, state.getRaceStorage().get(id)?.name ?: "Unknown")
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
