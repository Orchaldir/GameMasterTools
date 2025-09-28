package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.time.editCalendar
import at.orchaldir.gm.app.html.time.parseCalendar
import at.orchaldir.gm.app.html.time.showCalendar
import at.orchaldir.gm.app.html.util.showDate
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.selector.time.date.convertDate
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortElements
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$CALENDAR_TYPE")
class CalendarRoutes {
    @Resource("details")
    class Details(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("new")
    class New(val parent: CalendarRoutes = CalendarRoutes())

    @Resource("delete")
    class Delete(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("edit")
    class Edit(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("preview")
    class Preview(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())

    @Resource("update")
    class Update(val id: CalendarId, val parent: CalendarRoutes = CalendarRoutes())
}

fun Application.configureCalendarRouting() {
    routing {
        get<CalendarRoutes> {
            logger.info { "Get all calendars" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCalendars(call, STORE.getState())
            }
        }
        get<CalendarRoutes.Details> { details ->
            logger.info { "Get details of calendar ${details.id.value}" }

            val state = STORE.getState()
            val calendar = state.getCalendarStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarDetails(call, state, calendar)
            }
        }
        get<CalendarRoutes.New> {
            handleCreateElement(STORE.getState().getCalendarStorage()) { id ->
                CalendarRoutes.Edit(id)
            }
        }
        get<CalendarRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CalendarRoutes())
        }
        get<CalendarRoutes.Edit> { edit ->
            logger.info { "Get editor for calendar ${edit.id.value}" }

            val state = STORE.getState()
            val calendar = state.getCalendarStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarEditor(call, state, calendar)
            }
        }
        post<CalendarRoutes.Preview> { preview ->
            logger.info { "Preview changes to calendar ${preview.id.value}" }

            val state = STORE.getState()
            val calendar = parseCalendar(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarEditor(call, state, calendar)
            }
        }
        post<CalendarRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCalendar)
        }
    }
}

private fun HTML.showAllCalendars(call: ApplicationCall, state: State) {
    val calendars = state.sortElements(state.getCalendarStorage().getAll())
    val defaultCalendar = state.getDefaultCalendar()
    val count = calendars.size
    val createLink = call.application.href(CalendarRoutes.New())

    simpleHtml("Calendars") {
        field("Count", count)

        table {
            tr {
                th { +"Name" }
                th { +"Default" }
                th { +"Days" }
                th { +"Months" }
                th { +"Start in Default" }
                th { +"Today" }
            }
            calendars.forEach { calendar ->
                val example = convertDate(defaultCalendar, calendar, state.getCurrentDate())

                tr {
                    tdLink(call, state, calendar)
                    td {
                        if (calendar == defaultCalendar) {
                            +"yes"
                        }
                    }
                    tdSkipZero(calendar.getDaysPerYear())
                    tdSkipZero(calendar.getMonthsPerYear())
                    td { showDate(call, state, calendar.getStartDateInDefaultCalendar()) }
                    td { showDate(call, state, calendar, example) }
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCalendarDetails(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val backLink = call.application.href(CalendarRoutes())
    val deleteLink = call.application.href(CalendarRoutes.Delete(calendar.id))
    val editLink = call.application.href(CalendarRoutes.Edit(calendar.id))

    simpleHtmlDetails(calendar) {
        showCalendar(call, state, calendar)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showCalendarEditor(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val backLink = href(call, calendar.id)
    val previewLink = call.application.href(CalendarRoutes.Preview(calendar.id))
    val updateLink = call.application.href(CalendarRoutes.Update(calendar.id))

    simpleHtmlEditor(calendar, true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editCalendar(state, calendar)
            }
        }
    }
}
