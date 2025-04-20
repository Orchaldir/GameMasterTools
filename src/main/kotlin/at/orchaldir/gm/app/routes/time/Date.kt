package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.field
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonPhase
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.Coterminous
import at.orchaldir.gm.core.selector.getEvents
import at.orchaldir.gm.core.selector.getForHolidays
import at.orchaldir.gm.core.selector.time.date.*
import at.orchaldir.gm.core.selector.world.getPlanarAlignments
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ceilDiv
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

fun HTML.showDay(call: ApplicationCall, calendarId: CalendarId, day: Day) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayDay = calendar.resolveDay(day)

    showDate(call, calendarId, day, "Day") {
        visualizeMonth(call, state, calendar, displayDay.month, day)
    }
}

fun HTML.showWeek(call: ApplicationCall, calendarId: CalendarId, week: Week) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val month = calendar.resolveDay(calendar.getStartDayOfWeek(week)).month

    showDate(call, calendarId, week, "Week") {
        visualizeMonth(call, state, calendar, month, week)
    }
}

fun HTML.showMonth(call: ApplicationCall, calendarId: CalendarId, month: Month) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayMonth = calendar.resolveMonth(month)

    showDate(call, calendarId, month, "Month") {
        visualizeMonth(call, state, calendar, displayMonth)
    }
}

fun HTML.showDate(
    call: ApplicationCall,
    calendarId: CalendarId,
    date: Date,
    label: String,
    content: HtmlBlockTag.() -> Unit = {},
) {
    val state = STORE.getState()
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents(calendarId, date)
    val backLink = call.application.href(TimeRoutes())
    val upDate = calendar.moveUp(date)

    simpleHtml("$label: " + display(calendar, date)) {
        fieldLink("Calendar", call, state, calendar)

        if (date !is Day) {
            field(call, "Start", calendar, calendar.getStartDay(date))
            field(call, "End", calendar, calendar.getEndDay(date))
        }

        action { link(call, calendarId, date.next(), "Next $label") }
        action { link(call, calendarId, date.previous(), "Previous $label") }

        if (upDate != null) {
            action { link(call, calendarId, upDate, "Up") }
        }

        content()

        showEvents(events, call, state, calendar)

        back(backLink)
    }
}

private fun HtmlBlockTag.visualizeMonth(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
    displayMonth: DisplayMonth,
    selection: Date? = null,
) {
    when (calendar.days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> visualizeMonthWithWeekDays(
            call,
            state,
            calendar,
            displayMonth,
            selection,
            calendar.days,
        )
    }
}

private fun HtmlBlockTag.visualizeMonthWithWeekDays(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
    displayMonth: DisplayMonth,
    selection: Date?,
    days: Weekdays,
) {
    val moons = state.getMoonStorage().getAll()
    val month = calendar.getMonth(displayMonth)
    val startOfMonth = calendar.resolveDay(calendar.getStartDisplayDayOfMonth(displayMonth))
    val startWeek = calendar.moveUpDayToWeek(startOfMonth)
    val columns = days.weekDays + 1
    val checkSelection: HtmlBlockTag.(Day) -> Unit = if (selection != null) {
        val selectionStart = calendar.getStartDay(selection)
        val selectionEnd = calendar.getEndDay(selection)

        create(calendar, selectionStart, selectionEnd)
    } else {
        {}
    }

    table {
        tr {
            th {
                colSpan = columns.size.toString()
                +month.name
            }
        }
        tr {
            th { }
            days.weekDays.forEach {
                th {
                    +it.name
                }
            }
        }
        val startIndex = calendar.getWeekDay(startOfMonth) ?: 0
        var dayIndex = -startIndex
        val minDaysShown = startIndex + month.days
        val weeksShown = minDaysShown.ceilDiv(days.weekDays.size)
        var week = startWeek

        repeat(weeksShown) {
            tr {
                td {
                    val display = calendar.resolveWeek(week)
                    link(call, calendar.id, week, (display.weekIndex + 1).toString())

                    week += 1
                }
                repeat(days.weekDays.size) {
                    td {
                        if (month.isInside(dayIndex)) {
                            val day = startOfMonth + dayIndex

                            checkSelection(day)

                            link(call, calendar.id, day, (dayIndex + 1).toString())

                            showMoons(call, moons, day)

                            showList(state.getForHolidays(day)) { holiday ->
                                link(call, holiday)
                            }

                            val planes = state.getPlanarAlignments(day)
                                .filterValues { alignment -> alignment == Coterminous }

                            showMap(planes) { plane, alignment ->
                                link(call, plane)
                                +" ($alignment)"
                            }
                        }
                        dayIndex++
                    }
                }
            }
        }
    }
}

private fun create(
    calendar: Calendar,
    selectionStart: Day,
    selectionEnd: Day,
): HtmlBlockTag.(Day) -> Unit =
    { day ->
        if (day.isBetween(calendar, selectionStart, selectionEnd)) {
            style = "background-color:cyan"
        }
    }

private fun TD.showMoons(
    call: ApplicationCall,
    moons: Collection<Moon>,
    day: Day,
) {
    moons.forEach {
        when (it.getPhase(day)) {
            MoonPhase.NewMoon -> showIcon(call, it, "New Moon", "new-moon.svg")
            MoonPhase.FullMoon -> showIcon(call, it, "Full Moon", "full-moon.svg")
            else -> doNothing()
        }
    }
}

private fun TD.showIcon(
    call: ApplicationCall,
    moon: Moon,
    text: String,
    filename: String,
) {
    link(call, moon.id) {
        abbr {
            title = text
            img {
                src = "/static/$filename"
                width = "16p"
            }
        }
    }
}
