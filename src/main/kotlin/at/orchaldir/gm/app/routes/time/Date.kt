package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.field
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonPhase.FullMoon
import at.orchaldir.gm.core.model.world.moon.MoonPhase.NewMoon
import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.Coterminous
import at.orchaldir.gm.core.selector.getEvents
import at.orchaldir.gm.core.selector.getForHolidays
import at.orchaldir.gm.core.selector.time.date.*
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import at.orchaldir.gm.core.selector.world.getPlanarAlignments
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.ceilDiv
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

fun HTML.showDay(
    call: ApplicationCall,
    state: State,
    calendarId: CalendarId,
    day: Day,
) {
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayDay = calendar.resolveDay(day)

    showDate(call, state, calendarId, day, "Day") {
        visualizeMonth(call, state, calendar, displayDay.month, day)
    }
}

fun HTML.showWeek(
    call: ApplicationCall,
    state: State,
    calendarId: CalendarId,
    week: Week,
) {
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val month = calendar.resolveDay(calendar.getStartDayOfWeek(week)).month

    showDate(call, state, calendarId, week, "Week") {
        visualizeMonth(call, state, calendar, month, week)
    }
}

fun HTML.showMonth(
    call: ApplicationCall,
    state: State,
    calendarId: CalendarId,
    month: Month,
) {
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val displayMonth = calendar.resolveMonth(month)

    showDate(call, state, calendarId, month, "Month") {
        visualizeMonth(call, state, calendar, displayMonth)
    }
}

fun HTML.showDate(
    call: ApplicationCall,
    state: State,
    calendarId: CalendarId,
    date: Date,
    label: String,
    content: HtmlBlockTag.() -> Unit = {},
) {
    val calendar = state.getCalendarStorage().getOrThrow(calendarId)
    val events = state.getEvents(calendar, date)
    val backLink = call.application.href(TimeRoutes.ShowEvents())
    val upDate = calendar.moveUp(date)

    simpleHtml("$label: " + display(calendar, date)) {
        fieldLink("Calendar", call, state, calendar)

        if (date !is Day) {
            field(call, state, "Start", calendar, calendar.getStartDay(date))
            field(call, state, "End", calendar, calendar.getEndDay(date))
        } else if (calendarId != state.getDefaultCalendarId()) {
            val convertedDate = state.convertDateToDefault(calendar, date)
            field(call, state, "In default calendar", convertedDate)
        }

        addLinkAction(call, state, calendarId, "Next $label", date.next())
        addLinkAction(call, state, calendarId, "Previous $label", date.previous())

        if (upDate != null) {
            action { link(call, state, calendarId, upDate, "Up") }
        }

        content()

        br { }

        showEvents(events, call, state, calendar)

        back(backLink)
    }
}

private fun HtmlBlockTag.addLinkAction(
    call: ApplicationCall,
    state: State,
    calendarId: CalendarId,
    label: String,
    date: Date?,
) {
    if (date != null) {
        action { link(call, state, calendarId, date, label) }
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
                +month.name.text
            }
        }
        tr {
            th { }
            days.weekDays.forEach {
                th {
                    +it.name.text
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
        if (day.isOverlapping(calendar, selectionStart, selectionEnd)) {
            style = "background-color:cyan"
        }
    }

private fun TD.showMoons(
    call: ApplicationCall,
    moons: Collection<Moon>,
    day: Day,
) {
    val showName = moons.size > 1

    moons.forEach {
        when (it.getPhase(day)) {
            NewMoon -> showIcon(call, it, showName, "New Moon", "new-moon.svg")
            FullMoon -> showIcon(call, it, showName, "Full Moon", "full-moon.svg")
            else -> doNothing()
        }
    }
}

private fun TD.showIcon(
    call: ApplicationCall,
    moon: Moon,
    showName: Boolean,
    text: String,
    filename: String,
) {
    link(call, moon.id) {
        abbr {
            title = if (showName) {
                "$text (${moon.name})"
            } else {
                text
            }
            img {
                src = "/static/$filename"
                width = "16p"
            }
        }
    }
}
