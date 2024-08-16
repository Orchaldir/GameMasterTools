package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.selector.getDefaultCalendar
import kotlinx.html.*

fun HtmlBlockTag.field(state: State, label: String, date: Date) {
    field(label, state.getDefaultCalendar(), date)
}

fun HtmlBlockTag.field(label: String, calendar: Calendar, date: Date) {
    val calendarDate = calendar.resolve(date)

    field(label) {
        +calendar.display(calendarDate)
    }
}

fun FORM.selectDate(
    state: State,
    fieldLabel: String,
    date: Date,
    param: String,
) {
    selectDate(fieldLabel, state.getDefaultCalendar(), date, param)
}

fun FORM.selectDate(
    fieldLabel: String,
    calendar: Calendar,
    date: Date,
    param: String,
) {
    val displayDate = calendar.resolve(date)
    val dateTypeParam = combine(param, DATE)

    field(fieldLabel) {
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
                selectEraIndex(param, calendar, displayDate.eraIndex)
                selectYearIndex(param, displayDate.yearIndex)
                selectMonthIndex(param, calendar, displayDate.monthIndex)
                selectDayIndex(param, calendar, displayDate.monthIndex, displayDate.dayIndex)
            }

            is DisplayYear -> {
                selectEraIndex(param, calendar, displayDate.eraIndex)
                selectYearIndex(param, displayDate.yearIndex)
            }
        }
    }
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
        selectEraIndex(param, calendar, displayDate.eraIndex)
        selectYearIndex(param, displayDate.yearIndex)
        selectMonthIndex(param, calendar, displayDate.monthIndex)
        selectDayIndex(param, calendar, displayDate.monthIndex, displayDate.dayIndex)
    }
}

private fun P.selectEraIndex(
    param: String,
    calendar: Calendar,
    eraIndex: Int,
) {
    val eraParam = combine(param, ERA)

    select {
        id = eraParam
        name = eraParam
        onChange = ON_CHANGE_SCRIPT
        calendar.eras.getAll().withIndex().forEach { (index, era) ->
            option {
                label = era.text
                value = index.toString()
                selected = index == eraIndex
            }
        }
    }
}

private fun P.selectYearIndex(
    param: String,
    yearIndex: Int,
) {
    val yearParam = combine(param, YEAR)
    selectNumber(yearIndex + 1, 1, Int.MAX_VALUE, yearParam, true)
}

private fun P.selectMonthIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
) {
    val monthParam = combine(param, MONTH)

    select {
        id = monthParam
        name = monthParam
        onChange = ON_CHANGE_SCRIPT
        calendar.months.withIndex().forEach { (index, month) ->
            option {
                label = month.name
                value = index.toString()
                selected = index == monthIndex
            }
        }
    }
}

private fun P.selectDayIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    dayIndex: Int,
) {
    val month = calendar.months[monthIndex]
    selectNumber(dayIndex + 1, 1, month.days, combine(param, DAY), true)
}
