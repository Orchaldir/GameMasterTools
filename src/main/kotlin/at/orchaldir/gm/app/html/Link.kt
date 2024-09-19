package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.*
import at.orchaldir.gm.app.plugins.character.CharacterRoutes
import at.orchaldir.gm.app.plugins.race.RaceRoutes
import at.orchaldir.gm.app.plugins.world.MoonRoutes
import at.orchaldir.gm.app.plugins.world.MountainRoutes
import at.orchaldir.gm.app.plugins.world.RiverRoutes
import at.orchaldir.gm.app.plugins.world.town.TownRoutes
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.fashion.FashionId
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getName
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.A
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
) = link(href) { +text }

fun HtmlBlockTag.link(
    href: String,
    content: A.() -> Unit,
) = a(href) { content() }

// date

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: Calendar,
    date: Date,
) {
    val calendarDate = calendar.resolve(date)
    when (date) {
        is Day -> {
            link(call, date, calendar.display(calendarDate))
        }

        is Year -> {
            link(call, date, calendar.display(calendarDate))
        }
    }
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    day: Day,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowDay(day)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    year: Year,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowYear(year)), text)
}

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

fun <ID : Id<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    id: ID,
    content: A.() -> Unit,
) = link(href(call, id), content)

fun <ID : Id<ID>> href(
    call: ApplicationCall,
    id: ID,
) = when (id) {
    is CalendarId -> call.application.href(CalendarRoutes.Details(id))
    is CharacterId -> call.application.href(CharacterRoutes.Details(id))
    is CultureId -> call.application.href(CultureRoutes.Details(id))
    is FashionId -> call.application.href(FashionRoutes.Details(id))
    is HolidayId -> call.application.href(HolidayRoutes.Details(id))
    is ItemTemplateId -> call.application.href(ItemTemplateRoutes.Details(id))
    is LanguageId -> call.application.href(LanguageRoutes.Details(id))
    is MaterialId -> call.application.href(MaterialRoutes.Details(id))
    is MoonId -> call.application.href(MoonRoutes.Details(id))
    is MountainId -> call.application.href(MountainRoutes.Details(id))
    is NameListId -> call.application.href(NameListRoutes.Details(id))
    is PersonalityTraitId -> call.application.href(PersonalityTraitRoutes.Details(id))
    is RaceId -> call.application.href(RaceRoutes.Details(id))
    is RaceAppearanceId -> call.application.href(RaceRoutes.AppearanceRoutes.Details(id))
    is RiverId -> call.application.href(RiverRoutes.Details(id))
    is TownId -> call.application.href(TownRoutes.Details(id))
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
