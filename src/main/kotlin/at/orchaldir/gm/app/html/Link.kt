package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.app.routes.character.PersonalityTraitRoutes
import at.orchaldir.gm.app.routes.character.title.TitleRoutes
import at.orchaldir.gm.app.routes.culture.CultureRoutes
import at.orchaldir.gm.app.routes.culture.FashionRoutes
import at.orchaldir.gm.app.routes.culture.LanguageRoutes
import at.orchaldir.gm.app.routes.economy.BusinessRoutes
import at.orchaldir.gm.app.routes.economy.JobRoutes
import at.orchaldir.gm.app.routes.economy.MaterialRoutes
import at.orchaldir.gm.app.routes.economy.StandardOfLivingRoutes
import at.orchaldir.gm.app.routes.economy.money.CurrencyRoutes
import at.orchaldir.gm.app.routes.economy.money.CurrencyUnitRoutes
import at.orchaldir.gm.app.routes.illness.IllnessRoutes
import at.orchaldir.gm.app.routes.item.*
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes
import at.orchaldir.gm.app.routes.magic.SpellGroupRoutes
import at.orchaldir.gm.app.routes.magic.SpellRoutes
import at.orchaldir.gm.app.routes.organization.OrganizationRoutes
import at.orchaldir.gm.app.routes.race.RaceRoutes
import at.orchaldir.gm.app.routes.realm.*
import at.orchaldir.gm.app.routes.religion.DomainRoutes
import at.orchaldir.gm.app.routes.religion.GodRoutes
import at.orchaldir.gm.app.routes.religion.PantheonRoutes
import at.orchaldir.gm.app.routes.time.CalendarRoutes
import at.orchaldir.gm.app.routes.time.HolidayRoutes
import at.orchaldir.gm.app.routes.time.TimeRoutes
import at.orchaldir.gm.app.routes.utls.DataSourceRoutes
import at.orchaldir.gm.app.routes.utls.FontRoutes
import at.orchaldir.gm.app.routes.utls.NameListRoutes
import at.orchaldir.gm.app.routes.utls.QuoteRoutes
import at.orchaldir.gm.app.routes.world.*
import at.orchaldir.gm.app.routes.world.town.TownMapRoutes
import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.SpellId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.DomainId
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.region.RegionId
import at.orchaldir.gm.core.model.world.region.RiverId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.core.selector.time.date.resolve
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

// field

fun <ID : Id<ID>> HtmlBlockTag.fieldLink(
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    field(id.type()) {
        link(call, state, id)
    }
}

fun <ID : Id<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    field(label) {
        link(call, state, id)
    }
}

fun <ID : Id<ID>> HtmlBlockTag.optionalFieldLink(
    call: ApplicationCall,
    state: State,
    id: ID?,
) {
    if (id != null) {
        fieldLink(id.type(), call, state, id)
    }
}

fun <ID : Id<ID>> HtmlBlockTag.optionalFieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    id: ID?,
) {
    if (id != null) {
        fieldLink(label, call, state, id)
    }
}

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    element: ELEMENT,
) {
    field(label) {
        link(call, element)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) {
    field(label) {
        link(call, state, element)
    }
}

fun HtmlBlockTag.fieldLink(label: String, link: String, text: String) {
    p {
        b { +"$label: " }
        a(link) { +text }
    }
}

inline fun <reified T : Any> HtmlBlockTag.fieldStorageLink(call: ApplicationCall, storage: Storage<*, *>, link: T) {
    fieldLink(storage.getPlural(), call.application.href(link), "${storage.getSize()}")
}

// generic

fun HtmlBlockTag.back(href: String) = action(href, "Back")

fun HtmlBlockTag.action(
    href: String,
    text: String,
) = action { link(href, text) }

fun HtmlBlockTag.action(
    content: P.() -> Unit,
) = p { content() }

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
    state: State,
    calendar: Calendar,
    date: Date,
) {
    val calendarDate = calendar.resolve(date)

    link(call, state, calendar.id, date, display(calendar, calendarDate))
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    calendar: CalendarId,
    date: Date,
    text: String,
) {
    val years = state.getCalendarStorage()
        .getOrThrow(calendar)
        .getDurationInYears(date, state.getCurrentDate())

    span {
        title = "$years years ago"

        when (date) {
            is Day -> link(call, calendar, date, text)
            is DayRange -> link(call, calendar, date, text)
            is Week -> link(call, calendar, date, text)
            is Month -> link(call, calendar, date, text)
            is Year -> link(call, calendar, date, text)
            is Decade -> link(call, calendar, date, text)
            is Century -> link(call, calendar, date, text)
        }
    }
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    day: Day,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowDay(day, calendar)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    range: DayRange,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowDayRange(range.startDay.day, range.endDay.day, calendar)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    week: Week,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowWeek(week, calendar)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    month: Month,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowMonth(month, calendar)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    year: Year,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowYear(year, calendar)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    decade: Decade,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowDecade(decade, calendar)), text)
}

fun HtmlBlockTag.link(
    call: ApplicationCall,
    calendar: CalendarId,
    century: Century,
    text: String,
) {
    link(call.application.href(TimeRoutes.ShowCentury(century, calendar)), text)
}

// element

fun <ID : Id<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    link(call, id, state.getElementName(id))
}

fun <ID : Id<ID>> HtmlBlockTag.optionalLink(
    call: ApplicationCall,
    state: State,
    id: ID?,
) {
    if (id != null) {
        link(call, id, state.getElementName(id))
    }
}

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    element: ELEMENT,
) {
    link(call, element.id(), element.name())
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) {
    link(call, element.id(), element.name(state))
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.link(
    call: ApplicationCall,
    element: ELEMENT,
    text: String,
) = link(href(call, element.id()), text)

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
    is ArchitecturalStyleId -> call.application.href(ArchitecturalStyleRoutes.Details(id))
    is ArticleId -> call.application.href(ArticleRoutes.Details(id))
    is BattleId -> call.application.href(BattleRoutes.Details(id))
    is BuildingId -> call.application.href(BuildingRoutes.Details(id))
    is BusinessId -> call.application.href(BusinessRoutes.Details(id))
    is CalendarId -> call.application.href(CalendarRoutes.Details(id))
    is CatastropheId -> call.application.href(CatastropheRoutes.Details(id))
    is CharacterId -> call.application.href(CharacterRoutes.Details(id))
    is CultureId -> call.application.href(CultureRoutes.Details(id))
    is CurrencyId -> call.application.href(CurrencyRoutes.Details(id))
    is CurrencyUnitId -> call.application.href(CurrencyUnitRoutes.Details(id))
    is DataSourceId -> call.application.href(DataSourceRoutes.Details(id))
    is DomainId -> call.application.href(DomainRoutes.Details(id))
    is EquipmentId -> call.application.href(EquipmentRoutes.Details(id))
    is FashionId -> call.application.href(FashionRoutes.Details(id))
    is FontId -> call.application.href(FontRoutes.Details(id))
    is GodId -> call.application.href(GodRoutes.Details(id))
    is HolidayId -> call.application.href(HolidayRoutes.Details(id))
    is IllnessId -> call.application.href(IllnessRoutes.Details(id))
    is JobId -> call.application.href(JobRoutes.Details(id))
    is LanguageId -> call.application.href(LanguageRoutes.Details(id))
    is LegalCodeId -> call.application.href(LegalCodeRoutes.Details(id))
    is MagicTraditionId -> call.application.href(MagicTraditionRoutes.Details(id))
    is MaterialId -> call.application.href(MaterialRoutes.Details(id))
    is MoonId -> call.application.href(MoonRoutes.Details(id))
    is RegionId -> call.application.href(RegionRoutes.Details(id))
    is NameListId -> call.application.href(NameListRoutes.Details(id))
    is OrganizationId -> call.application.href(OrganizationRoutes.Details(id))
    is PantheonId -> call.application.href(PantheonRoutes.Details(id))
    is PeriodicalId -> call.application.href(PeriodicalRoutes.Details(id))
    is PeriodicalIssueId -> call.application.href(PeriodicalIssueRoutes.Details(id))
    is PersonalityTraitId -> call.application.href(PersonalityTraitRoutes.Details(id))
    is PlaneId -> call.application.href(PlaneRoutes.Details(id))
    is QuoteId -> call.application.href(QuoteRoutes.Details(id))
    is RaceId -> call.application.href(RaceRoutes.Details(id))
    is RaceAppearanceId -> call.application.href(RaceRoutes.AppearanceRoutes.Details(id))
    is RealmId -> call.application.href(RealmRoutes.Details(id))
    is RiverId -> call.application.href(RiverRoutes.Details(id))
    is SpellId -> call.application.href(SpellRoutes.Details(id))
    is SpellGroupId -> call.application.href(SpellGroupRoutes.Details(id))
    is StandardOfLivingId -> call.application.href(StandardOfLivingRoutes.Details(id))
    is StreetId -> call.application.href(StreetRoutes.Details(id))
    is StreetTemplateId -> call.application.href(StreetTemplateRoutes.Details(id))
    is TextId -> call.application.href(TextRoutes.Details(id))
    is TitleId -> call.application.href(TitleRoutes.Details(id))
    is TownId -> call.application.href(TownRoutes.Details(id))
    is TownMapId -> call.application.href(TownMapRoutes.Details(id))
    is TreatyId -> call.application.href(TreatyRoutes.Details(id))
    is UniformId -> call.application.href(UniformRoutes.Details(id))
    is WarId -> call.application.href(WarRoutes.Details(id))
    else -> error("Cannot create link for unsupported type ${id.type()}!")
}
