package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.selector.getDefaultCalendar
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
        is DisplayDay -> {
            selectDay(param, calendar, displayDate, minDate)
        }

        is DisplayYear -> {
            selectYear(param, calendar, displayDate, minDate)
        }
    }
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
    val displayMinYear = minDate?.let {
        when (it) {
            is Day -> calendar.resolve(it).year
            is Year -> calendar.resolve(it)
        }
    }
    val displayMaxYear = maxDate?.let {
        when (it) {
            is Day -> calendar.resolve(it).year
            is Year -> calendar.resolve(it)
        }
    }

    selectEraIndex(param, calendar, year, displayMinYear, displayMaxYear)
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
    val displayMinDay = minDate?.let {
        when (it) {
            is Day -> calendar.resolve(it)
            is Year -> calendar.resolve(calendar.getStartOfYear(it))
        }
    }

    selectEraIndex(param, calendar, displayDate.year, displayMinDay?.year)
    selectYearIndex(param, displayDate.year, displayMinDay?.year)
    selectMonthIndex(param, calendar, displayDate, displayMinDay)
    selectDayIndex(param, calendar, displayDate, displayMinDay)
}

private fun HtmlBlockTag.selectEraIndex(
    param: String,
    calendar: Calendar,
    year: DisplayYear,
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
                selected = index == year.eraIndex
            }
        }
    }
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

    selectInt(year.yearIndex + 1, minIndex, maxIndex, yearParam, true)
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
    selectWithIndex(combine(param, MONTH), calendar.months) { index, month ->
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
    minMonthIndex: Int,
) {
    val month = calendar.months[monthIndex]
    selectInt(dayIndex + 1, minMonthIndex + 1, month.days, combine(param, DAY), true)
}
