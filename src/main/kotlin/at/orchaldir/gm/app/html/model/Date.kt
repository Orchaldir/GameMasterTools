package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseBool
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.resolve
import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.selector.getDefaultCalendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

fun HtmlBlockTag.optionalField(call: ApplicationCall, state: State, label: String, date: Date?) {
    if (date != null) {
        field(call, label, state.getDefaultCalendar(), date)
    }
}

fun HtmlBlockTag.field(call: ApplicationCall, state: State, label: String, date: Date) {
    field(call, label, state.getDefaultCalendar(), date)
}

fun HtmlBlockTag.field(call: ApplicationCall, label: String, calendar: Calendar, date: Date) {
    field(label) {
        link(call, calendar, date)
    }
}

fun HtmlBlockTag.showCurrentDate(
    call: ApplicationCall,
    state: State,
) {
    field(call, state, "Current Date", state.time.currentDate)
}

fun HtmlBlockTag.showOptionalDate(call: ApplicationCall, state: State, date: Date?) {
    if (date != null) {
        showDate(call, state, date)
    }
}

fun HtmlBlockTag.showDate(call: ApplicationCall, state: State, date: Date) {
    link(call, state.getDefaultCalendar(), date)
}

fun displayDate(state: State, date: Date): String {
    val calendar = state.getDefaultCalendar()
    val calendarDate = calendar.resolve(date)

    return calendar.display(calendarDate)
}

// select

fun HtmlBlockTag.selectOptionalDate(
    state: State,
    fieldLabel: String,
    date: Date?,
    param: String,
    minDate: Date? = null,
) {
    selectOptionalDate(state.getDefaultCalendar(), fieldLabel, date, param, minDate)
}

private fun HtmlBlockTag.selectOptionalDate(
    calendar: Calendar,
    fieldLabel: String,
    date: Date?,
    param: String,
    minDate: Date? = null,
) {
    field(fieldLabel) {
        selectBool(date != null, combine(param, AVAILABLE), isDisabled = false, update = true)
        if (date != null) {
            selectDate(calendar, date, param, minDate)
        }
    }
}

fun HtmlBlockTag.selectOptionalYear(
    state: State,
    fieldLabel: String,
    year: Year?,
    param: String,
    minDate: Date? = null,
) {
    selectOptionalYear(state.getDefaultCalendar(), fieldLabel, year, param, minDate)
}

fun HtmlBlockTag.selectOptionalYear(
    calendar: Calendar,
    fieldLabel: String,
    year: Year?,
    param: String,
    minDate: Date? = null,
) {
    field(fieldLabel) {
        selectBool(year != null, combine(param, AVAILABLE), isDisabled = false, update = true)
        if (year != null) {
            val displayYear = calendar.resolve(year)
            selectYear(param, calendar, displayYear, minDate)
        }
    }
}

fun HtmlBlockTag.selectDate(
    state: State,
    fieldLabel: String,
    date: Date,
    param: String,
    minDate: Date? = null,
) {
    selectDate(state.getDefaultCalendar(), fieldLabel, date, param, minDate)
}

private fun HtmlBlockTag.selectDate(
    calendar: Calendar,
    fieldLabel: String,
    date: Date,
    param: String,
    minDate: Date? = null,
) {
    field(fieldLabel) {
        selectDate(calendar, date, param, minDate)
    }
}

private fun HtmlBlockTag.selectDate(
    calendar: Calendar,
    date: Date,
    param: String,
    minDate: Date? = null,
) {
    val displayDate = calendar.resolve(date)
    val dateTypeParam = combine(param, DATE)

    select {
        id = dateTypeParam
        name = dateTypeParam
        onChange = ON_CHANGE_SCRIPT
        DateType.entries.forEach {
            option {
                label = it.name
                value = it.name
                selected = it == date.getType()
            }
        }
    }
    when (displayDate) {
        is DisplayDay -> selectDay(param, calendar, displayDate, minDate)
        is DisplayYear -> selectYear(param, calendar, displayDate, minDate)
        is DisplayDecade -> selectDecade(param, calendar, displayDate, minDate)
    }
}

private fun HtmlBlockTag.selectDecade(
    param: String,
    calendar: Calendar,
    decade: DisplayDecade,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val displayMinYear = minDate?.let { calendar.getDisplayYear(it) }
    val displayMaxYear = maxDate?.let { calendar.getDisplayYear(it) }

    selectEraIndex(param, calendar, decade.eraIndex, displayMinYear, displayMaxYear)
    selectDecadeIndex(param, calendar, decade, minDate, maxDate)
}

fun FORM.selectYear(
    state: State,
    fieldLabel: String,
    year: Year,
    param: String,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    selectYear(fieldLabel, state.getDefaultCalendar(), year, param, minDate, maxDate)
}

fun FORM.selectYear(
    fieldLabel: String,
    calendar: Calendar,
    year: Year,
    param: String,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val displayDate = calendar.resolve(year)

    field(fieldLabel) {
        selectYear(param, calendar, displayDate, minDate, maxDate)
    }
}

private fun HtmlBlockTag.selectYear(
    param: String,
    calendar: Calendar,
    year: DisplayYear,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val displayMinYear = minDate?.let { calendar.getDisplayYear(it) }
    val displayMaxYear = maxDate?.let { calendar.getDisplayYear(it) }

    selectEraIndex(param, calendar, year.eraIndex, displayMinYear, displayMaxYear)
    selectYearIndex(param, year, displayMinYear, displayMaxYear)
}

fun FORM.selectDay(
    state: State,
    fieldLabel: String,
    day: Day,
    param: String,
) {
    selectDay(fieldLabel, state.getDefaultCalendar(), day, param)
}

fun FORM.selectDay(
    fieldLabel: String,
    calendar: Calendar,
    day: Day,
    param: String,
) {
    val displayDate = calendar.resolve(day)

    field(fieldLabel) {
        selectDay(param, calendar, displayDate, null)
    }
}

private fun HtmlBlockTag.selectDay(
    param: String,
    calendar: Calendar,
    displayDate: DisplayDay,
    minDate: Date?,
) {
    val displayMinDay = minDate?.let { calendar.getDisplayDay(it) }

    selectEraIndex(param, calendar, displayDate.year.eraIndex, displayMinDay?.year)
    selectYearIndex(param, displayDate.year, displayMinDay?.year)
    selectMonthIndex(param, calendar, displayDate, displayMinDay)
    selectDayIndex(param, calendar, displayDate, displayMinDay)
}

private fun HtmlBlockTag.selectEraIndex(
    param: String,
    calendar: Calendar,
    eraIndex: Int,
    minYear: DisplayYear? = null,
    maxYear: DisplayYear? = null,
) {
    val eraParam = combine(param, ERA)
    val minIndex = minYear?.eraIndex ?: 0
    val maxIndex = maxYear?.eraIndex ?: Int.MAX_VALUE

    select {
        id = eraParam
        name = eraParam
        onChange = ON_CHANGE_SCRIPT
        calendar.eras.getAll().withIndex().forEach { (index, era) ->
            option {
                label = era.text
                value = index.toString()
                disabled = index < minIndex || index > maxIndex
                selected = index == eraIndex
            }
        }
    }
}

private fun HtmlBlockTag.selectDecadeIndex(
    param: String,
    calendar: Calendar,
    decade: DisplayDecade,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val decadeParam = combine(param, DECADE)
    val minIndex = if (minDate != null) {
        val minDecade = calendar.getDisplayDecade(minDate)

        if (minDecade.eraIndex == decade.eraIndex) {
            minDecade.eraIndex
        } else {
            0
        }
    } else {
        0
    }
    val maxIndex = if (maxDate != null) {
        val maxDecade = calendar.getDisplayDecade(maxDate)

        if (maxDecade.eraIndex == decade.eraIndex) {
            maxDecade.decadeIndex
        } else {
            Int.MAX_VALUE
        }
    } else {
        Int.MAX_VALUE
    }

    selectInt(decade.decadeIndex, minIndex, maxIndex, 1, decadeParam, true)
    +"0s"
}

private fun HtmlBlockTag.selectYearIndex(
    param: String,
    year: DisplayYear,
    minYear: DisplayYear? = null,
    maxYear: DisplayYear? = null,
) {
    val yearParam = combine(param, YEAR)
    val minIndex = if (minYear != null) {
        if (minYear.eraIndex == year.eraIndex) {
            minYear.yearIndex
        } else {
            0
        }
    } else {
        0
    } + 1
    val maxIndex = if (maxYear != null) {
        if (maxYear.eraIndex == year.eraIndex) {
            maxYear.yearIndex + 1
        } else {
            Int.MAX_VALUE
        }
    } else {
        Int.MAX_VALUE
    }

    selectInt(year.yearIndex + 1, minIndex, maxIndex, 1, yearParam, true)
}

fun HtmlBlockTag.selectMonthIndex(
    label: String,
    param: String,
    calendar: Calendar,
    monthIndex: Int,
) {
    field(label) {
        selectMonthIndex(param, calendar, monthIndex, 0)
    }
}

private fun HtmlBlockTag.selectMonthIndex(
    param: String,
    calendar: Calendar,
    day: DisplayDay,
    minDay: DisplayDay? = null,
) {
    val minIndex = if (minDay != null && day.year == minDay.year) {
        minDay.monthIndex
    } else {
        0
    }

    selectMonthIndex(param, calendar, day.monthIndex, minIndex)
}

private fun HtmlBlockTag.selectMonthIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    minMonthIndex: Int,
) {
    selectWithIndex(combine(param, MONTH), calendar.months.months()) { index, month ->
        label = month.name
        value = index.toString()
        selected = monthIndex == index
        disabled = index < minMonthIndex
    }
}

fun HtmlBlockTag.selectDayIndex(
    label: String,
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    dayIndex: Int,
) {
    field(label) {
        selectDayIndex(param, calendar, monthIndex, dayIndex, 0)
    }
}

fun HtmlBlockTag.selectDayIndex(
    label: String,
    param: String,
    dayIndex: Int,
    minDayIndex: Int,
    maxDayIndex: Int,
) {
    field(label) {
        selectDayIndex(param, dayIndex, minDayIndex, maxDayIndex)
    }
}

private fun HtmlBlockTag.selectDayIndex(
    param: String,
    calendar: Calendar,
    day: DisplayDay,
    minDay: DisplayDay? = null,
) {
    val minIndex = if (minDay != null && day.year == minDay.year && day.monthIndex == minDay.monthIndex) {
        minDay.dayIndex
    } else {
        0
    }

    selectDayIndex(param, calendar, day.monthIndex, day.dayIndex, minIndex)
}

private fun HtmlBlockTag.selectDayIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    dayIndex: Int,
    minDayIndex: Int,
) {
    val month = calendar.months.getMonth(monthIndex)
    selectDayIndex(param, dayIndex, minDayIndex, month.days - 1)
}

fun HtmlBlockTag.selectDayIndex(
    param: String,
    dayIndex: Int,
    minDayIndex: Int,
    maxDayIndex: Int,
) {
    selectInt(dayIndex + 1, minDayIndex + 1, maxDayIndex + 1, 1, combine(param, DAY), true)
}

// parse

fun parseOptionalDate(
    parameters: Parameters,
    state: State,
    param: String,
): Date? = parseOptionalDate(parameters, state.getDefaultCalendar(), param)

fun parseOptionalDate(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Date? {
    if (!parseBool(parameters, combine(param, AVAILABLE))) {
        return null
    }

    return when (parse(parameters, combine(param, DATE), DateType.Year)) {
        DateType.Day -> parseDay(parameters, calendar, param)
        DateType.Year -> parseYear(parameters, calendar, param)
        DateType.Decade -> parseDecade(parameters, calendar, param)
    }
}

fun parseOptionalYear(
    parameters: Parameters,
    state: State,
    param: String,
): Year? = parseOptionalYear(parameters, state.getDefaultCalendar(), param)

fun parseOptionalYear(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Year? {
    if (!parseBool(parameters, combine(param, AVAILABLE))) {
        return null
    }

    return parseYear(parameters, calendar, param)
}

fun parseDate(
    parameters: Parameters,
    state: State,
    param: String,
    default: Date? = null,
): Date = parseDate(parameters, state.getDefaultCalendar(), param, default)

fun parseDate(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
    default: Date? = null,
): Date {
    if (default != null && !parameters.contains(combine(param, ERA))) {
        return default
    }

    return when (parse(parameters, combine(param, DATE), DateType.Year)) {
        DateType.Day -> parseDay(parameters, calendar, param)
        DateType.Year -> parseYear(parameters, calendar, param)
        DateType.Decade -> parseDecade(parameters, calendar, param)
    }
}

fun parseDay(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
    default: Day? = null,
): Day {
    val eraParam = combine(param, ERA)

    if (default != null && !parameters.contains(eraParam)) {
        return default
    }

    val eraIndex = parseInt(parameters, eraParam)
    val yearIndex = parseInt(parameters, combine(param, YEAR), 1) - 1
    val monthIndex = parseInt(parameters, combine(param, MONTH))
    val dayIndex = parseDayIndex(parameters, param)
    val calendarDate = DisplayDay(eraIndex, yearIndex, monthIndex, dayIndex)

    return calendar.resolve(calendarDate)
}

fun parseDayIndex(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, DAY), 1) - 1

fun parseYear(
    parameters: Parameters,
    state: State,
    param: String,
): Year = parseYear(parameters, state.getDefaultCalendar(), param)

fun parseYear(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Year {
    val eraIndex = parseInt(parameters, combine(param, ERA))
    val yearIndex = parseInt(parameters, combine(param, YEAR), 1) - 1
    val calendarDate = DisplayYear(eraIndex, yearIndex)

    return calendar.resolve(calendarDate)
}

fun parseDecade(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Decade {
    val eraIndex = parseInt(parameters, combine(param, ERA))
    val decadeIndex = parseInt(parameters, combine(param, DECADE))
    val calendarDate = DisplayDecade(eraIndex, decadeIndex)

    return calendar.resolve(calendarDate)
}