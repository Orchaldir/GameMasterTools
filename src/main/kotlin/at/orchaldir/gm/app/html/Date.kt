package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.date.*
import at.orchaldir.gm.core.selector.getDefaultCalendar
import kotlinx.html.*

fun HtmlBlockTag.field(state: State, label: String, date: Date) {
    field(label, state.calendars.getOrThrow(CalendarId(0)), date)
}

fun HtmlBlockTag.field(label: String, calendar: Calendar, date: Date) {
    val calendarDate = calendar.resolve(date)

    field(label) {
        +calendar.eras.display(calendarDate)
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
    val yearParam = combine(param, YEAR)

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
                selectEra(calendar, displayDate.eraIndex, param)
                selectYear(yearParam, displayDate.yearIndex)
                selectMonth(param, calendar, displayDate.monthIndex)
                selectDay(param, calendar, displayDate.monthIndex, displayDate.dayIndex)
            }

            is DisplayYear -> {
                selectEra(calendar, displayDate.eraIndex, param)
                selectYear(yearParam, displayDate.yearIndex)
            }
        }
    }
}

private fun P.selectEra(
    calendar: Calendar,
    eraIndex: Int,
    param: String,
) {
    val eraParam = combine(param, ERA)

    select {
        id = eraParam
        name = eraParam
        calendar.eras.getAll().withIndex().forEach { (index, era) ->
            option {
                label = era.text
                value = index.toString()
                selected = index == eraIndex
            }
        }
    }
}

private fun P.selectYear(
    yearParam: String,
    yearIndex: Int,
) {
    selectNumber(yearIndex + 1, 1, Int.MAX_VALUE, yearParam, true)
}

private fun P.selectMonth(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
) {
    val monthParam = combine(param, MONTH)

    select {
        id = monthParam
        name = monthParam
        calendar.months.withIndex().forEach { (index, month) ->
            option {
                label = month.name
                value = index.toString()
                selected = index == monthIndex
            }
        }
    }
}

private fun P.selectDay(
    param: String,
    calendar: Calendar,
    monthIndex: Int,
    dayIndex: Int,
) {
    val month = calendar.months[monthIndex]
    selectNumber(dayIndex + 1, 1, month.days, combine(param, DAY), false)
}
