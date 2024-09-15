package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.plugins.TimeRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.time.*
import at.orchaldir.gm.core.selector.getDefaultCalendar
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

fun HtmlBlockTag.field(call: ApplicationCall, state: State, label: String, date: Date) {
    field(call, label, state.getDefaultCalendar(), date)
}

fun HtmlBlockTag.field(call: ApplicationCall, label: String, calendar: Calendar, date: Date) {
    field(label) {
        link(call, calendar, date)
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
    selectInt(yearIndex + 1, 1, Int.MAX_VALUE, yearParam, true)
}

fun HtmlBlockTag.selectMonthIndex(
    label: String,
    param: String,
    calendar: Calendar,
    monthIndex: Int,
) {
    field(label) {
        selectMonthIndex(param, calendar, monthIndex)
    }
}

private fun P.selectMonthIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
) {
    selectWithIndex(combine(param, MONTH), calendar.months) { index, month ->
        label = month.name
        value = index.toString()
        selected = monthIndex == index
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
        selectDayIndex(param, calendar, monthIndex, dayIndex)
    }
}

private fun P.selectDayIndex(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    dayIndex: Int,
) {
    val month = calendar.months[monthIndex]
    selectInt(dayIndex + 1, 1, month.days, combine(param, DAY), true)
}
