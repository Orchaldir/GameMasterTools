package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.calendar.CalendarOriginType.Improved
import at.orchaldir.gm.core.model.time.calendar.CalendarOriginType.Original
import at.orchaldir.gm.core.model.time.date.DisplayYear
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.getHolidays
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.time.calendar.*
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.p

// show


fun HtmlBlockTag.showCalendar(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val cultures = state.getCultures(calendar.id)
    val holidays = state.getHolidays(calendar.id)
    val periodicals = state.getPeriodicals(calendar.id)

    showOrigin(call, state, calendar)

    h2 { +"Parts" }

    showDays(calendar)
    showMonths(calendar)

    h2 { +"Eras" }
    showEras(call, state, calendar)

    h2 { +"Usage" }

    fieldList(call, state, cultures)
    fieldList("Holidays", holidays) { holiday ->
        link(call, holiday)
        +": "
        +holiday.relativeDate.display(calendar)
    }
    fieldList(call, state, periodicals)

    showDateFormat(calendar.defaultFormat)
}

private fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val children = state.getChildren(calendar.id)

    when (calendar.origin) {
        is ImprovedCalendar -> {
            field("Origin", "Improved")
            fieldLink("Parent Calendar", call, state, calendar.origin.parent)
        }

        OriginalCalendar -> {
            field("Origin", "Original")
        }
    }
    fieldList(call, state, "Child Calendars", children)
}

private fun HtmlBlockTag.showDays(
    calendar: Calendar,
) {
    field("Days", calendar.days.getType())
    when (calendar.days) {
        is Weekdays -> fieldList("Weekdays", calendar.days.weekDays) { day ->
            +day.name
        }

        DayOfTheMonth -> doNothing()
    }
}

private fun HtmlBlockTag.showMonths(calendar: Calendar) {
    when (val months = calendar.months) {
        is ComplexMonths -> fieldList("Months", months.months) { month ->
            field(month.name, "${month.days} days")
        }

        is SimpleMonths -> {
            fieldList("Months", months.months) { month ->
                +month
            }
            field("Days per Month", months.daysPerMonth)
        }
    }

    field("Months per Year", calendar.months.getSize())
    field("Days per Year", calendar.getDaysPerYear())
}

private fun HtmlBlockTag.showEras(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    field(call, state, "Start Date", calendar.getStartDateInDefaultCalendar())
    field("Before Era", display(calendar, DisplayYear(0, 0)))
    field("Current Era", display(calendar, DisplayYear(1, 0)))
}

// edit

fun FORM.editCalendar(
    state: State,
    calendar: Calendar,
) {
    val holidays = state.getHolidays(calendar.id)

    selectName(calendar.name)
    editOrigin(state, calendar)

    h2 { +"Parts" }

    editDays(calendar, holidays)
    editMonths(calendar, holidays)

    h2 { +"Eras" }

    editEras(calendar, state)

    editDateFormat(calendar.defaultFormat)
}

private fun FORM.editDays(
    calendar: Calendar,
    holidays: List<Holiday>,
) {
    val days = calendar.days
    val supportsDayOfTheMonth = supportsDayOfTheMonth(holidays)

    selectValue("Days", DAYS, DaysType.entries, days.getType(), true) {
        it == DaysType.DayOfTheMonth && !supportsDayOfTheMonth
    }
    when (days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> {
            val minNumber = getMinNumberOfWeekdays(holidays)
            selectInt("Weekdays", days.weekDays.size, minNumber, 100, 1, combine(WEEK, DAYS), true)
            days.weekDays.withIndex().forEach { (index, day) ->
                p {
                    selectText(day.name, combine(WEEK, DAY, index))
                }
            }
        }
    }
}

private fun FORM.editMonths(calendar: Calendar, holidays: List<Holiday>) {
    val minMonths = getMinNumberOfMonths(holidays)
    selectValue("Months Type", combine(MONTHS, TYPE), MonthsType.entries, calendar.months.getType(), true)
    selectInt("Months", calendar.months.getSize(), minMonths, 100, 1, MONTHS, true)

    when (val months = calendar.months) {
        is ComplexMonths -> months.months.withIndex().forEach { (index, month) ->
            val minDays = getMinNumberOfDays(holidays, index)
            p {
                selectText(month.name, combine(MONTH, NAME, index))
                +": "
                selectInt(month.days, minDays, 100, 1, combine(MONTH, DAYS, index), true)
                +"days"
            }
        }

        is SimpleMonths -> {
            val minDays = getMinNumberOfDays(holidays)

            months.months.withIndex().forEach { (index, month) ->
                p {
                    selectText(month, combine(MONTH, NAME, index))
                }
            }
            selectInt("Days per Month", months.daysPerMonth, minDays, 100, 1, combine(MONTH, DAYS))
        }
    }

    field("Days per Year", calendar.getDaysPerYear())
}

private fun FORM.editOrigin(
    state: State,
    calendar: Calendar,
) {
    val origin = calendar.origin
    val possibleParents = state.getPossibleParents(calendar.id)

    selectValue("Origin", ORIGIN, CalendarOriginType.entries, origin.getType(), true) {
        when (it) {
            Improved -> possibleParents.isEmpty()
            Original -> false
        }
    }
    when (origin) {
        is ImprovedCalendar -> selectElement(state, "Parent", CALENDAR_TYPE, possibleParents, origin.parent)
        else -> doNothing()
    }
}

private fun FORM.editEras(
    calendar: Calendar,
    state: State,
) {
    editEra("Before", calendar.eras.before, BEFORE)
    editEra("Current", calendar.eras.first, CURRENT)
    selectDate(state, "Start Date", calendar.getStartDateInDefaultCalendar(), CURRENT)
}

private fun FORM.editEra(
    label: String,
    era: CalendarEra,
    param: String,
) {
    selectNotEmptyString("$label Era - Name", era.text, combine(param, NAME))
    selectBool("$label Era - Is prefix", era.isPrefix, combine(param, PREFIX))
}

// parse

fun parseCalendarId(parameters: Parameters, param: String) = CalendarId(parseInt(parameters, param))

fun parseCalendar(
    parameters: Parameters,
    default: Calendar,
    id: CalendarId,
) = Calendar(
    id,
    parseName(parameters),
    parseDays(parameters),
    parseMonths(parameters),
    parseEras(parameters, default),
    parseOrigin(parameters),
    parseDateFormat(parameters),
)

private fun parseEras(parameters: Parameters, default: Calendar) = CalendarEras(
    parseBeforeStart(parameters),
    parseFirstEra(parameters, default),
)

private fun parseBeforeStart(parameters: Parameters) =
    EraBeforeStart(
        parseEraName(parameters, BEFORE),
        parseIsPrefix(parameters, BEFORE),
    )

private fun parseFirstEra(parameters: Parameters, default: Calendar) =
    LaterEra(
        parseDay(parameters, default, CURRENT),
        parseEraName(parameters, CURRENT),
        parseIsPrefix(parameters, CURRENT),
    )

private fun parseIsPrefix(parameters: Parameters, param: String) =
    parseBool(parameters, combine(param, PREFIX))

private fun parseEraName(parameters: Parameters, param: String) =
    parseNotEmptyString(parameters, combine(param, NAME), "?")

private fun parseDays(parameters: Parameters) = when (parse(parameters, DAYS, DaysType.DayOfTheMonth)) {
    DaysType.DayOfTheMonth -> DayOfTheMonth
    DaysType.Weekdays -> Weekdays(parseWeekdays(parameters))
}

private fun parseWeekdays(parameters: Parameters): List<WeekDay> {
    val count = parseInt(parameters, combine(WEEK, DAYS), 2)

    return (0..<count)
        .map { parseOptionalString(parameters, combine(WEEK, DAY, it)) ?: "${it + 1}.Day" }
        .map { WeekDay(it) }
}

private fun parseMonths(parameters: Parameters) = when (parse(parameters, combine(MONTHS, TYPE), MonthsType.Simple)) {
    MonthsType.Simple -> {
        val count = parseInt(parameters, MONTHS, 2)

        SimpleMonths(
            parseDaysPerMonth(parameters, combine(MONTH, DAYS)),
            (0..<count)
                .map { parseMonthName(parameters, it) },
        )
    }

    MonthsType.Complex -> {
        val count = parseInt(parameters, MONTHS, 2)

        ComplexMonths(
            (0..<count)
                .map { parseComplexMonth(parameters, it) }
        )
    }
}

private fun parseComplexMonth(parameters: Parameters, it: Int) = MonthDefinition(
    parseMonthName(parameters, it),
    parseDaysPerMonth(parameters, combine(MONTH, DAYS, it)),
)

private fun parseDaysPerMonth(parameters: Parameters, param: String) = parseInt(parameters, param, 2)

private fun parseMonthName(parameters: Parameters, it: Int) =
    parseOptionalString(parameters, combine(MONTH, NAME, it)) ?: "${it + 1}.Month"

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Improved -> {
        val parent = parseCalendarId(parameters, CALENDAR_TYPE)
        ImprovedCalendar(parent)
    }

    Original -> OriginalCalendar
}