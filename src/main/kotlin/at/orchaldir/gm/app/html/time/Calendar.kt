package at.orchaldir.gm.app.html.time

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.model.time.date.DisplayYear
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.time.*
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show


fun HtmlBlockTag.showCalendar(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val cultures = state.getCultures(calendar.id)
    val holidays = state.getHolidays(calendar.id)
    val periodicals = state.getPeriodicals(calendar.id)

    optionalField(call, state, "Date", calendar.date)
    showOrigin(call, state, calendar)
    showDays(calendar)
    showMonths(calendar)
    showEras(call, state, calendar)
    showDateFormat(calendar.defaultFormat)

    h2 { +"Usage" }

    fieldElements(call, state, cultures)
    fieldList("Holidays", holidays) { holiday ->
        link(call, holiday)
        +": "
        +holiday.relativeDate.display(calendar)
    }
    fieldElements(call, state, periodicals)
}

private fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val children = state.getChildren(calendar.id)

    fieldOrigin(call, state, calendar.origin, ::CalendarId)
    fieldElements(call, state, "Child Calendars", children)
}

private fun HtmlBlockTag.showDays(
    calendar: Calendar,
) {
    h2 { +"Days" }

    field("Days", calendar.days.getType())
    when (calendar.days) {
        is Weekdays -> fieldList("Weekdays", calendar.days.weekDays) { day ->
            +day.name.text
        }

        DayOfTheMonth -> doNothing()
    }
}

private fun HtmlBlockTag.showMonths(calendar: Calendar) {
    h2 { +"Months" }

    when (val months = calendar.months) {
        is ComplexMonths -> table {
            tr {
                th { +"Month" }
                th { +"Name" }
                th { +"Title" }
                th { +"Days" }
            }
            months.months.withIndex().forEach { (index, month) ->
                tr {
                    tdSkipZero(index + 1)
                    tdString(month.name)
                    tdString(month.title)
                    tdSkipZero(month.days)
                }
            }
        }

        is SimpleMonths -> {
            table {
                tr {
                    th { +"Month" }
                    th { +"Name" }
                    th { +"Title" }
                }
                months.months.withIndex().forEach { (index, month) ->
                    tr {
                        tdSkipZero(index + 1)
                        tdString(month.name)
                        tdString(month.title)
                    }
                }
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
    h2 { +"Eras" }

    field(call, state, "Start Date", calendar.getStartDateInDefaultCalendar())
    showBeforeEra(calendar)
    showCurrentEra(calendar)
}

private fun HtmlBlockTag.showBeforeEra(calendar: Calendar) {
    field("Before Era", display(calendar, DisplayYear(0, 0)))
}

private fun HtmlBlockTag.showCurrentEra(calendar: Calendar) {
    field("Current Era", display(calendar, DisplayYear(1, 0)))
}

// edit

fun HtmlBlockTag.editCalendar(
    state: State,
    calendar: Calendar,
) {
    val holidays = state.getHolidays(calendar.id)

    selectName(calendar.name)
    selectOptionalDate(state, "Date", calendar.date, DATE)
    editOrigin(
        state,
        calendar.id,
        calendar.origin,
        null,
        ALLOWED_CALENDAR_ORIGINS,
        ::CalendarId,
    )
    editDays(calendar, holidays)
    editMonths(calendar, holidays)
    editEras(calendar, state)
    editDateFormat(calendar.defaultFormat)
}

private fun HtmlBlockTag.editDays(
    calendar: Calendar,
    holidays: List<Holiday>,
) {
    h2 { +"Days" }

    val days = calendar.days
    val supportsDayOfTheMonth = supportsDayOfTheMonth(holidays)

    selectValue("Days", DAYS, DaysType.entries, days.getType()) {
        it == DaysType.DayOfTheMonth && !supportsDayOfTheMonth
    }
    when (days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> {
            val minNumber = getMinNumberOfWeekdays(holidays)
            selectInt("Weekdays", days.weekDays.size, minNumber, 100, 1, combine(WEEK, DAYS))
            days.weekDays.withIndex().forEach { (index, day) ->
                p {
                    selectName(day.name, combine(WEEK, DAY, index))
                }
            }
        }
    }
}

private fun HtmlBlockTag.editMonths(calendar: Calendar, holidays: List<Holiday>) {
    val minMonths = getMinNumberOfMonths(holidays)

    h2 { +"Months" }

    selectValue("Months Type", combine(MONTHS, TYPE), MonthsType.entries, calendar.months.getType())
    selectInt("Months", calendar.months.getSize(), minMonths, 100, 1, MONTHS)

    when (val months = calendar.months) {
        is ComplexMonths -> {
            table {
                tr {
                    th { +"Month" }
                    th { +"Name" }
                    th { +"Title" }
                    th { +"Days" }
                }
                months.months.withIndex().forEach { (index, month) ->
                    val minDays = getMinNumberOfDays(holidays, index)

                    tr {
                        tdSkipZero(index + 1)
                        selectMonthName(index, month.name)
                        selectMonthTitle(index, month.title)
                        selectDaysOfMonth(index, month, minDays)
                    }
                }
            }
        }

        is SimpleMonths -> {
            val minDays = getMinNumberOfDays(holidays)

            table {
                tr {
                    th { +"Month" }
                    th { +"Name" }
                    th { +"Title" }
                }
                months.months.withIndex().forEach { (index, month) ->
                    tr {
                        tdSkipZero(index + 1)
                        selectMonthName(index, month.name)
                        selectMonthTitle(index, month.title)
                    }
                }
            }
            selectInt("Days per Month", months.daysPerMonth, minDays, 100, 1, combine(MONTH, DAYS))
        }
    }

    field("Days per Year", calendar.getDaysPerYear())
}

private fun TR.selectMonthName(
    index: Int,
    name: Name,
) {
    td {
        p {
            selectName(name, combine(MONTH, NAME, index))
        }
    }
}

private fun TR.selectMonthTitle(
    index: Int,
    title: Name?,
) {
    td {
        p {
            selectOptionalName("Title", title, combine(MONTH, TITLE, index))
        }
    }
}

private fun TR.selectDaysOfMonth(
    index: Int,
    month: MonthDefinition,
    minDays: Int,
) {
    td {
        p {
            selectInt(
                "Days",
                month.days,
                minDays,
                100,
                1,
                combine(MONTH, DAYS, index),
            )
        }
    }
}

private fun HtmlBlockTag.editEras(
    calendar: Calendar,
    state: State,
) {
    h2 { +"Eras" }

    showBeforeEra(calendar)
    editEra("Before", calendar.eras.before, BEFORE)
    showCurrentEra(calendar)
    editEra("Current", calendar.eras.first, CURRENT)
    selectDate(state, "Start Date", calendar.getStartDateInDefaultCalendar(), CURRENT)
}

private fun HtmlBlockTag.editEra(
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
    state: State,
    parameters: Parameters,
    id: CalendarId,
) = Calendar(
    id,
    parseName(parameters),
    parseDays(parameters),
    parseMonths(parameters),
    parseEras(parameters, state.getDefaultCalendar()),
    parseOptionalDate(parameters, state, DATE),
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
        .map { parseName(parameters, combine(WEEK, DAY, it), "${it + 1}.Day") }
        .map { WeekDay(it) }
}

private fun parseMonths(parameters: Parameters) = when (parse(parameters, combine(MONTHS, TYPE), MonthsType.Simple)) {
    MonthsType.Simple -> {
        val count = parseInt(parameters, MONTHS, 2)

        SimpleMonths(
            parseDaysPerMonth(parameters, combine(MONTH, DAYS)),
            (0..<count)
                .map { parseSimpleMonth(parameters, it) },
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

private fun parseSimpleMonth(parameters: Parameters, index: Int) = SimpleMonthDefinition(
    parseMonthName(parameters, index),
    parseMonthTitle(parameters, index)
)

private fun parseComplexMonth(parameters: Parameters, index: Int) = MonthDefinition(
    parseMonthName(parameters, index),
    parseDaysPerMonth(parameters, combine(MONTH, DAYS, index)),
    parseMonthTitle(parameters, index)
)

private fun parseDaysPerMonth(parameters: Parameters, param: String) = parseInt(parameters, param, 2)

private fun parseMonthName(parameters: Parameters, index: Int) =
    parseName(parameters, combine(MONTH, NAME, index), "${index + 1}.Month")

private fun parseMonthTitle(parameters: Parameters, index: Int) =
    parseOptionalName(parameters, combine(MONTH, TITLE, index))