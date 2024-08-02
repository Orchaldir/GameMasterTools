package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateCalendar
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/calendars")
class Calendars {
    @Resource("details")
    class Details(val id: CalendarId, val parent: Calendars = Calendars())

    @Resource("new")
    class New(val parent: Calendars = Calendars())

    @Resource("delete")
    class Delete(val id: CalendarId, val parent: Calendars = Calendars())

    @Resource("edit")
    class Edit(val id: CalendarId, val parent: Calendars = Calendars())

    @Resource("preview")
    class Preview(val id: CalendarId, val parent: Calendars = Calendars())

    @Resource("update")
    class Update(val id: CalendarId, val parent: Calendars = Calendars())
}

fun Application.configureCalendarRouting() {
    routing {
        get<Calendars> {
            logger.info { "Get all calendars" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCalendars(call)
            }
        }
        get<Calendars.Details> { details ->
            logger.info { "Get details of calendar ${details.id.value}" }

            val state = STORE.getState()
            val calendar = state.calendars.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarDetails(call, state, calendar)
            }
        }
        get<Calendars.New> {
            logger.info { "Add new calendar" }

            STORE.dispatch(CreateCalendar)

            call.respondRedirect(call.application.href(Calendars.Edit(STORE.getState().calendars.lastId)))

            STORE.getState().save()
        }
        get<Calendars.Delete> { delete ->
            logger.info { "Delete calendar ${delete.id.value}" }

            STORE.dispatch(DeleteCalendar(delete.id))

            call.respondRedirect(call.application.href(Calendars()))

            STORE.getState().save()
        }
        get<Calendars.Edit> { edit ->
            logger.info { "Get editor for calendar ${edit.id.value}" }

            val state = STORE.getState()
            val calendar = state.calendars.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCalendarEditor(call, state, calendar)
            }
        }
        post<Calendars.Preview> { preview ->
            logger.info { "Preview changes to calendar ${preview.id.value}" }

            val calendar = parseCalendar(call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()

                showCalendarEditor(call, state, calendar)
            }
        }
        post<Calendars.Update> { update ->
            logger.info { "Update calendar ${update.id.value}" }

            val calendar = parseCalendar(call.receiveParameters(), update.id)

            STORE.dispatch(UpdateCalendar(calendar))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCalendars(call: ApplicationCall) {
    val calendars = STORE.getState().calendars.getAll().sortedBy { it.name }
    val count = calendars.size
    val createLink = call.application.href(Calendars.New())

    simpleHtml("Calendars") {
        field("Count", count.toString())
        showList(calendars) { calendar ->
            link(call, calendar)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCalendarDetails(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val backLink = call.application.href(Calendars())
    val deleteLink = call.application.href(Calendars.Delete(calendar.id))
    val editLink = call.application.href(Calendars.Edit(calendar.id))
    val children = state.getChildren(calendar.id)

    simpleHtml("Calendar: ${calendar.name}") {
        field("Id", calendar.id.value.toString())
        field("Name", calendar.name)
        when (calendar.origin) {
            is ImprovedCalendar -> {
                field("Origin", "Improved")
                field("Parent Calendar") {
                    link(call, state, calendar.origin.parent)
                }
            }

            OriginalCalendar -> {
                field("Origin", "Original")
            }
        }
        showList("Child Calendars", children) { calendar ->
            link(call, calendar)
        }
        p { a(editLink) { +"Edit" } }
        if (state.canDelete(calendar.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showCalendarEditor(
    call: ApplicationCall,
    state: State,
    calendar: Calendar,
) {
    val backLink = href(call, calendar.id)
    val previewLink = call.application.href(Calendars.Preview(calendar.id))
    val updateLink = call.application.href(Calendars.Update(calendar.id))

    simpleHtml("Edit Calendar: ${calendar.name}") {
        field("Id", calendar.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                b { +"Name: " }
                textInput(name = NAME) {
                    value = calendar.name
                }
            }
            editOrigin(state, calendar)
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun FORM.editOrigin(
    state: State,
    calendar: Calendar,
) {
    val origin = calendar.origin
    val possibleParents = state.getPossibleParents(calendar.id)

    field("Origin") {
        select {
            id = ORIGIN
            name = ORIGIN
            onChange = ON_CHANGE_SCRIPT
            CalendarOriginType.entries.forEach {
                option {
                    label = it.name
                    value = it.name
                    disabled = when (it) {
                        CalendarOriginType.Improved -> possibleParents.isEmpty()
                        CalendarOriginType.Original -> false
                    }
                    selected = when (it) {
                        CalendarOriginType.Improved -> origin is ImprovedCalendar
                        CalendarOriginType.Original -> origin is OriginalCalendar
                    }
                }
            }
        }
    }
    when (origin) {
        is ImprovedCalendar ->
            selectEnum("Parent", CALENDAR, possibleParents) { c ->
                label = c.name
                value = c.id.value.toString()
                selected = origin.parent == c.id
            }

        else -> doNothing()
    }
}