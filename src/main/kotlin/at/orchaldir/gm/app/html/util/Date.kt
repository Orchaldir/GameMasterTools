package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.*
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.core.selector.time.getCurrentDate
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show optional

fun HtmlBlockTag.optionalField(call: ApplicationCall, state: State, label: String, date: Date?) =
    optionalField(call, state, state.getDefaultCalendar(), label, date)

fun HtmlBlockTag.optionalField(
    call: ApplicationCall,
    state: State,
    calendarId: CalendarId,
    label: String,
    date: Date?,
) {
    if (date != null) {
        val calendar = state.getCalendarStorage().getOrThrow(calendarId)
        field(call, state, label, calendar, date)
    }
}

fun HtmlBlockTag.optionalField(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
    label: String,
    date: Date?,
) {
    if (date != null) {
        field(call, state, label, calendar, date)
    }
}

fun HtmlBlockTag.showOptionalDate(call: ApplicationCall, state: State, calendarId: CalendarId, date: Date?) {
    if (date != null) {
        showDate(call, state, calendarId, date)
    }
}

fun HtmlBlockTag.showOptionalDate(call: ApplicationCall, state: State, date: Date?) {
    if (date != null) {
        showDate(call, state, date)
    }
}

// show

fun HtmlBlockTag.field(call: ApplicationCall, state: State, label: String, date: Date) {
    field(call, state, label, state.getDefaultCalendar(), date)
}

fun HtmlBlockTag.field(call: ApplicationCall, state: State, calendarId: CalendarId, label: String, date: Date) {
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    field(call, state, label, calendar, date)
}

fun HtmlBlockTag.field(
    call: ApplicationCall,
    state: State,
    label: String,
    calendar: Calendar,
    date: Date,
) {
    field(label) {
        link(call, state, calendar, date)
    }
}

fun HtmlBlockTag.fieldCurrentDate(
    call: ApplicationCall,
    state: State,
) {
    field(call, state, "Current Date", state.getCurrentDate())
}

fun HtmlBlockTag.showDate(call: ApplicationCall, state: State, date: Date) {
    showDate(call, state, state.getDefaultCalendar(), date)
}

fun HtmlBlockTag.showDate(call: ApplicationCall, state: State, calendarId: CalendarId, date: Date) {
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    showDate(call, state, calendar, date)
}

fun HtmlBlockTag.showDate(call: ApplicationCall, state: State, calendar: Calendar, date: Date) {
    val years = calendar.getDurationInYears(date, state.getCurrentDate())

    span {
        title = "$years years ago"
        link(call, state, calendar, date)
    }
}

fun displayDate(state: State, date: Date): String {
    val calendar = state.getDefaultCalendar()

    return display(calendar, date)
}

// show age

fun HtmlBlockTag.fieldAge(name: String, state: State, date: Date?) {
    if (date != null) {
        fieldAge(name, state.getAgeInYears(date))
    }
}

fun HtmlBlockTag.fieldAge(name: String, age: Int) = field(name) {
    +"$age years"
}

// select optional

fun HtmlBlockTag.selectOptionalDate(
    state: State,
    fieldLabel: String,
    date: Date?,
    param: String,
    minDate: Date? = null,
) {
    selectOptionalDate(state.getDefaultCalendar(), fieldLabel, date, param, minDate)
}

fun HtmlBlockTag.selectOptionalDate(
    calendar: Calendar,
    fieldLabel: String,
    date: Date?,
    param: String,
    minDate: Date? = null,
) {
    selectOptional(fieldLabel, date, param) {
        selectDate(calendar, it, param, minDate)
    }
}

fun HtmlBlockTag.selectOptionalYear(
    state: State,
    fieldLabel: String,
    year: Year?,
    param: String,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    selectOptionalYear(state.getDefaultCalendar(), fieldLabel, year, param, minDate, maxDate)
}

fun HtmlBlockTag.selectOptionalYear(
    calendar: Calendar,
    fieldLabel: String,
    year: Year?,
    param: String,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    selectOptional(fieldLabel, year, param) {
        val displayYear = calendar.resolveYear(it)
        selectYear(param, calendar, displayYear, minDate, maxDate)
    }
}

fun HtmlBlockTag.selectOptionalMonth(
    calendar: Calendar,
    fieldLabel: String,
    month: Month?,
    param: String,
    minDate: Date? = null,
) {
    selectOptional(fieldLabel, month, param) {
        val displayMonth = calendar.resolveMonth(it)
        selectMonth(param, calendar, displayMonth, minDate)
    }
}

fun HtmlBlockTag.selectOptionalWeek(
    calendar: Calendar,
    fieldLabel: String,
    week: Week?,
    param: String,
    minDate: Date? = null,
) {
    selectOptional(fieldLabel, week, param) {
        val displayWeek = calendar.resolveWeek(it)
        selectWeek(param, calendar, displayWeek, minDate)
    }
}

fun HtmlBlockTag.selectOptionalDay(
    calendar: Calendar,
    fieldLabel: String,
    day: Day?,
    param: String,
    minDate: Date? = null,
) {
    selectOptional(fieldLabel, day, param) {
        val displayDay = calendar.resolveDay(it)
        selectDay(param, calendar, displayDay, minDate)
    }
}


// select

fun HtmlBlockTag.selectDate(
    state: State,
    fieldLabel: String,
    date: Date,
    param: String,
    minDate: Date? = null,
    optionalDateTypes: Set<DateType>? = null,
) {
    selectDate(state.getDefaultCalendar(), fieldLabel, date, param, minDate, optionalDateTypes)
}

fun HtmlBlockTag.selectDate(
    calendar: Calendar,
    fieldLabel: String,
    date: Date,
    param: String,
    minDate: Date? = null,
    optionalDateTypes: Set<DateType>? = null,
) {
    field(fieldLabel) {
        selectDate(calendar, date, param, minDate, optionalDateTypes)
    }
}

private fun HtmlBlockTag.selectDate(
    calendar: Calendar,
    date: Date,
    param: String,
    minDate: Date? = null,
    optionalDateTypes: Set<DateType>? = null,
) {
    val displayDate = calendar.resolve(date)
    val dateTypeParam = combine(param, DATE)
    val dateTypes = optionalDateTypes ?: calendar.getValidDateTypes()

    select {
        id = dateTypeParam
        name = dateTypeParam
        onChange = ON_CHANGE_SCRIPT
        dateTypes.forEach {
            option {
                label = it.name
                value = it.name
                selected = it == date.getType()
            }
        }
    }
    when (displayDate) {
        is DisplayDay -> selectDay(param, calendar, displayDate, minDate)
        is DisplayDayRange -> error("Day Range is not supported!")
        is DisplayWeek -> selectWeek(param, calendar, displayDate, minDate)
        is DisplayMonth -> selectMonth(param, calendar, displayDate, minDate)
        is DisplayYear -> selectYear(param, calendar, displayDate, minDate)
        is DisplayDecade -> selectDecade(param, calendar, displayDate, minDate)
        is DisplayCentury -> selectCentury(param, calendar, displayDate, minDate)
    }
}

private fun HtmlBlockTag.selectCentury(
    param: String,
    calendar: Calendar,
    century: DisplayCentury,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val displayMinYear = minDate?.let { calendar.getStartDisplayYear(it) }
    val displayMaxYear = maxDate?.let { calendar.getStartDisplayYear(it) }

    selectEraIndex(param, calendar, century.eraIndex, displayMinYear, displayMaxYear)
    selectCenturyIndex(param, calendar, century, minDate, maxDate)
}

private fun HtmlBlockTag.selectDecade(
    param: String,
    calendar: Calendar,
    decade: DisplayDecade,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val displayMinYear = minDate?.let { calendar.getStartDisplayYear(it) }
    val displayMaxYear = maxDate?.let { calendar.getStartDisplayYear(it) }

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
    val displayDate = calendar.resolveYear(year)

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
    val displayMinYear = minDate?.let { calendar.getStartDisplayYear(it) }
    val displayMaxYear = maxDate?.let { calendar.getStartDisplayYear(it) }

    selectEraIndex(param, calendar, year.eraIndex, displayMinYear, displayMaxYear)
    selectYearIndex(param, year, displayMinYear, displayMaxYear)
}

fun FORM.selectWeek(
    fieldLabel: String,
    calendar: Calendar,
    week: Week,
    param: String,
    minDate: Date? = null,
) {
    val displayDate = calendar.resolveWeek(week)

    field(fieldLabel) {
        selectWeek(param, calendar, displayDate, minDate)
    }
}

private fun HtmlBlockTag.selectWeek(
    param: String,
    calendar: Calendar,
    displayDate: DisplayWeek,
    minDate: Date?,
) {
    if (minDate != null) {
        val minDay = calendar.getStartDay(minDate)
        val displayMinDay = calendar.resolveDay(minDay)

        selectEraIndex(param, calendar, displayDate.year.eraIndex, displayMinDay.month.year)
        selectYearIndex(param, displayDate.year, displayMinDay.month.year)
        selectWeekIndex(param, calendar, displayDate, Pair(minDay, displayMinDay))
    } else {
        selectEraIndex(param, calendar, displayDate.year.eraIndex)
        selectYearIndex(param, displayDate.year)
        selectWeekIndex(param, calendar, displayDate)
    }
}

fun FORM.selectMonth(
    fieldLabel: String,
    calendar: Calendar,
    month: Month,
    param: String,
    minDate: Date? = null,
) {
    val displayDate = calendar.resolveMonth(month)

    field(fieldLabel) {
        selectMonth(param, calendar, displayDate, minDate)
    }
}

private fun HtmlBlockTag.selectMonth(
    param: String,
    calendar: Calendar,
    displayDate: DisplayMonth,
    minDate: Date?,
) {
    val displayMinDay = minDate?.let { calendar.getStartDisplayDay(it) }

    selectEraIndex(param, calendar, displayDate.year.eraIndex, displayMinDay?.month?.year)
    selectYearIndex(param, displayDate.year, displayMinDay?.month?.year)
    selectMonthIndex(param, calendar, displayDate, displayMinDay?.month)
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
    minDate: Date? = null,
) {
    val displayDate = calendar.resolveDay(day)

    field(fieldLabel) {
        selectDay(param, calendar, displayDate, minDate)
    }
}

private fun HtmlBlockTag.selectDay(
    param: String,
    calendar: Calendar,
    displayDate: DisplayDay,
    minDate: Date?,
) {
    val displayMinDay = minDate?.let { calendar.getStartDisplayDay(it) }

    selectEraIndex(param, calendar, displayDate.month.year.eraIndex, displayMinDay?.month?.year)
    selectYearIndex(param, displayDate.month.year, displayMinDay?.month?.year)
    selectMonthIndex(param, calendar, displayDate.month, displayMinDay?.month)
    selectDayIndex(param, calendar, displayDate, displayMinDay)
}

// select indices

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
                label = era.text.text
                value = index.toString()
                disabled = index < minIndex || index > maxIndex
                selected = index == eraIndex
            }
        }
    }
}

private fun HtmlBlockTag.selectCenturyIndex(
    param: String,
    calendar: Calendar,
    century: DisplayCentury,
    minDate: Date? = null,
    maxDate: Date? = null,
) {
    val decadeParam = combine(param, CENTURY)
    val minIndex = if (minDate != null) {
        val minCentury = calendar.getDisplayCentury(minDate)

        if (minCentury.eraIndex == century.eraIndex) {
            minCentury.centuryIndex
        } else {
            0
        }
    } else {
        0
    }
    val maxIndex = if (maxDate != null) {
        val maxCentury = calendar.getDisplayCentury(maxDate)

        if (maxCentury.eraIndex == century.eraIndex) {
            maxCentury.centuryIndex
        } else {
            Int.MAX_VALUE
        }
    } else {
        Int.MAX_VALUE
    }

    selectInt(century.centuryIndex, minIndex, maxIndex, 1, decadeParam)
    +"xx"
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
        val minDecade = calendar.getStartDisplayDecade(minDate)

        if (minDecade.eraIndex == decade.eraIndex) {
            minDecade.decadeIndex
        } else {
            0
        }
    } else {
        0
    }
    val maxIndex = if (maxDate != null) {
        val maxDecade = calendar.getStartDisplayDecade(maxDate)

        if (maxDecade.eraIndex == decade.eraIndex) {
            maxDecade.decadeIndex
        } else {
            Int.MAX_VALUE
        }
    } else {
        Int.MAX_VALUE
    }

    selectInt(decade.decadeIndex, minIndex, maxIndex, 1, decadeParam)
    +"0s"
}

private fun HtmlBlockTag.selectYearIndex(
    param: String,
    year: DisplayYear,
    minYear: DisplayYear? = null,
    maxYear: DisplayYear? = null,
) {
    val yearParam = combine(param, YEAR)
    val minYear = if (minYear != null) {
        if (minYear.eraIndex == year.eraIndex) {
            minYear.yearIndex
        } else {
            0
        }
    } else {
        0
    } + 1
    val maxYear = if (maxYear != null) {
        if (maxYear.eraIndex == year.eraIndex) {
            maxYear.yearIndex + 1
        } else {
            Int.MAX_VALUE
        }
    } else {
        Int.MAX_VALUE
    }

    selectInt(year.yearIndex + 1, minYear, maxYear, 1, yearParam)
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
    month: DisplayMonth,
    minMonth: DisplayMonth? = null,
) {
    val minIndex = if (minMonth != null && month.year == minMonth.year) {
        minMonth.monthIndex
    } else {
        0
    }

    selectMonthIndex(param, calendar, month.monthIndex, minIndex)
}

private fun HtmlBlockTag.selectMonthIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    minMonthIndex: Int,
) {
    selectWithIndex(combine(param, MONTH), calendar.months.months()) { index, month ->
        label = month.name.text
        value = index.toString()
        selected = monthIndex == index
        disabled = index < minMonthIndex
    }
}

private fun HtmlBlockTag.selectWeekIndex(
    param: String,
    calendar: Calendar,
    week: DisplayWeek,
    min: Pair<Day, DisplayDay>? = null,
) {
    val year = calendar.resolveYear(week.year)
    val startWeek = calendar.getStartWeekOfYear(year)
    val endWeek = calendar.getEndWeekOfYear(year)
    val minWeek = if (min != null && week.year == min.second.month.year) {
        calendar.moveUpDayToWeek(min.first)
    } else {
        startWeek
    }
    val minIndex = calendar.resolveWeek(minWeek).weekIndex
    val maxIndex = calendar.resolveWeek(endWeek).weekIndex

    selectWeekIndex(param, week.weekIndex, minIndex, maxIndex)
}

private fun HtmlBlockTag.selectWeekIndex(
    param: String,
    weekIndex: Int,
    minWeekIndex: Int,
    maxWeekIndex: Int,
) {
    selectInt(
        weekIndex + 1,
        minWeekIndex + 1,
        maxWeekIndex + 1,
        1,
        combine(param, WEEK),
    )
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
    val minIndex = if (minDay != null && day.month == minDay.month) {
        minDay.dayIndex
    } else {
        0
    }

    selectDayIndex(param, calendar, day.month.monthIndex, day.dayIndex, minIndex)
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
    selectInt(dayIndex + 1, minDayIndex + 1, maxDayIndex + 1, 1, combine(param, DAY))
}

// parse optional

fun parseOptionalDate(
    parameters: Parameters,
    state: State,
    param: String,
): Date? = parseOptionalDate(parameters, state.getDefaultCalendar(), param)

fun parseOptionalDate(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Date? = parseOptional(parameters, param) {
    parseDate(parameters, calendar, param)
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
): Year? = parseOptional(parameters, param) {
    parseYear(parameters, calendar, param)
}

fun parseOptionalMonth(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Month? = parseOptional(parameters, param) {
    parseMonth(parameters, calendar, param)
}

fun parseOptionalWeek(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Week? = parseOptional(parameters, param) {
    parseWeek(parameters, calendar, param)
}

fun parseOptionalDay(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Day? = parseOptional(parameters, param) {
    parseDay(parameters, calendar, param)
}

// parse

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
        DateType.DayRange -> error("Day Range is not supported!")
        DateType.Week -> parseWeek(parameters, calendar, param)
        DateType.Month -> parseMonth(parameters, calendar, param)
        DateType.Year -> parseYear(parameters, calendar, param)
        DateType.Decade -> parseDecade(parameters, calendar, param)
        DateType.Century -> parseCentury(parameters, calendar, param)
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

    val eraIndex = parseEraIndex(parameters, param)
    val yearIndex = parseYearIndex(parameters, param)
    val monthIndex = parseMonthIndex(parameters, param)
    val dayIndex = parseDayIndex(parameters, param)
    val calendarDate = DisplayDay(eraIndex, yearIndex, monthIndex, dayIndex)

    return calendar.resolveDay(calendarDate)
}

fun parseWeek(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Week {
    val eraIndex = parseEraIndex(parameters, param)
    val yearIndex = parseYearIndex(parameters, param)
    val weekIndex = parseWeekIndex(parameters, param)
    val calendarDate = DisplayWeek(eraIndex, yearIndex, weekIndex)

    return calendar.resolveWeek(calendarDate)
}

fun parseMonth(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Month {
    val eraIndex = parseEraIndex(parameters, param)
    val yearIndex = parseYearIndex(parameters, param)
    val monthIndex = parseMonthIndex(parameters, param)
    val calendarDate = DisplayMonth(eraIndex, yearIndex, monthIndex)

    return calendar.resolveMonth(calendarDate)
}

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
    val eraIndex = parseEraIndex(parameters, param)
    val yearIndex = parseYearIndex(parameters, param)
    val calendarDate = DisplayYear(eraIndex, yearIndex)

    return calendar.resolveYear(calendarDate)
}

fun parseDecade(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Decade {
    val eraIndex = parseEraIndex(parameters, param)
    val decadeIndex = parseDecadeIndex(parameters, param)
    val calendarDate = DisplayDecade(eraIndex, decadeIndex)

    return calendar.resolveDecade(calendarDate)
}

fun parseCentury(
    parameters: Parameters,
    calendar: Calendar,
    param: String,
): Century {
    val eraIndex = parseEraIndex(parameters, param)
    val centuryIndex = parseCenturyIndex(parameters, param)
    val calendarDate = DisplayCentury(eraIndex, centuryIndex)

    return calendar.resolveCentury(calendarDate)
}

fun parseDayIndex(parameters: Parameters, param: String) =
    parseIndexFromInt(parameters, param, DAY)

private fun parseWeekIndex(parameters: Parameters, param: String) =
    parseIndexFromInt(parameters, param, WEEK)

private fun parseMonthIndex(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, MONTH))

private fun parseYearIndex(parameters: Parameters, param: String) =
    parseIndexFromInt(parameters, param, YEAR)

private fun parseDecadeIndex(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, DECADE))

private fun parseCenturyIndex(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, CENTURY))

private fun parseEraIndex(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, ERA), 1)

private fun parseIndexFromInt(parameters: Parameters, param: String, type: String) =
    parseInt(parameters, combine(param, type), 1) - 1
