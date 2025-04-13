package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.time.editCalendar
import at.orchaldir.gm.app.html.model.time.parseCalendar
import at.orchaldir.gm.app.html.model.time.showCalendar
import at.orchaldir.gm.core.action.CreateCalendar
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.selector.time.calendar.canDelete
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.form
import kotlinx.html.id
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
                showAllCalendars(call)
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
            logger.info { "Add new calendar" }

            STORE.dispatch(CreateCalendar)

            call.respondRedirect(
                call.application.href(
                    CalendarRoutes.Edit(
                        STORE.getState().getCalendarStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<CalendarRoutes.Delete> { delete ->
            logger.info { "Delete calendar ${delete.id.value}" }

            STORE.dispatch(DeleteCalendar(delete.id))

            call.respondRedirect(call.application.href(CalendarRoutes()))

            STORE.getState().save()
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
            val calendar = parseCalendar(call.receiveParameters(), state.getDefaultCalendar(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarEditor(call, state, calendar)
            }
        }
        post<CalendarRoutes.Update> { update ->
            logger.info { "Update calendar ${update.id.value}" }

            val state = STORE.getState()
            val calendar = parseCalendar(call.receiveParameters(), state.getDefaultCalendar(), update.id)

            STORE.dispatch(UpdateCalendar(calendar))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCalendars(call: ApplicationCall) {
    val calendars = STORE.getState().getCalendarStorage().getAll().sortedBy { it.name }
    val count = calendars.size
    val createLink = call.application.href(CalendarRoutes.New())

    simpleHtml("Calendars") {
        field("Count", count)
        showList(calendars) { calendar ->
            link(call, calendar)
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

    simpleHtml("Calendar: ${calendar.name}") {
        showCalendar(call, state, calendar)

        action(editLink, "Edit")

        if (state.canDelete(calendar.id)) {
            action(deleteLink, "Delete")
        }

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

    simpleHtml("Edit Calendar: ${calendar.name}") {
        formWithPreview(previewLink, updateLink, backLink) {
            editCalendar(state, calendar)
        }
    }
}
