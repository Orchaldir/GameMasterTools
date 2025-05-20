package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.time.displayHolidayPurpose
import at.orchaldir.gm.app.html.time.editHoliday
import at.orchaldir.gm.app.html.time.parseHoliday
import at.orchaldir.gm.app.html.time.showHoliday
import at.orchaldir.gm.core.action.CreateHoliday
import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.holiday.HOLIDAY_TYPE
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.selector.time.canDelete
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortHolidays
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

@Resource("/$HOLIDAY_TYPE")
class HolidayRoutes {
    @Resource("details")
    class Details(val id: HolidayId, val parent: HolidayRoutes = HolidayRoutes())

    @Resource("new")
    class New(val parent: HolidayRoutes = HolidayRoutes())

    @Resource("delete")
    class Delete(val id: HolidayId, val parent: HolidayRoutes = HolidayRoutes())

    @Resource("edit")
    class Edit(val id: HolidayId, val parent: HolidayRoutes = HolidayRoutes())

    @Resource("preview")
    class Preview(val id: HolidayId, val parent: HolidayRoutes = HolidayRoutes())

    @Resource("update")
    class Update(val id: HolidayId, val parent: HolidayRoutes = HolidayRoutes())
}

fun Application.configureHolidayRouting() {
    routing {
        get<HolidayRoutes> {
            logger.info { "Get all holidays" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllHolidays(call, STORE.getState())
            }
        }
        get<HolidayRoutes.Details> { details ->
            logger.info { "Get details of holiday ${details.id.value}" }

            val state = STORE.getState()
            val holiday = state.getHolidayStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayDetails(call, state, holiday)
            }
        }
        get<HolidayRoutes.New> {
            logger.info { "Add new holiday" }

            STORE.dispatch(CreateHoliday)

            call.respondRedirect(call.application.href(HolidayRoutes.Edit(STORE.getState().getHolidayStorage().lastId)))

            STORE.getState().save()
        }
        get<HolidayRoutes.Delete> { delete ->
            logger.info { "Delete holiday ${delete.id.value}" }

            STORE.dispatch(DeleteHoliday(delete.id))

            call.respondRedirect(call.application.href(HolidayRoutes()))

            STORE.getState().save()
        }
        get<HolidayRoutes.Edit> { edit ->
            logger.info { "Get editor for holiday ${edit.id.value}" }

            val state = STORE.getState()
            val holiday = state.getHolidayStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayEditor(call, state, holiday)
            }
        }
        post<HolidayRoutes.Preview> { preview ->
            logger.info { "Get preview for holiday ${preview.id.value}" }

            val holiday = parseHoliday(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayEditor(call, STORE.getState(), holiday)
            }
        }
        post<HolidayRoutes.Update> { update ->
            logger.info { "Update holiday ${update.id.value}" }

            val holiday = parseHoliday(update.id, call.receiveParameters())

            STORE.dispatch(UpdateHoliday(holiday))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllHolidays(
    call: ApplicationCall,
    state: State,
) {
    val calendar = state.getDefaultCalendar()
    val holidays = state.sortHolidays()
    val createLink = call.application.href(HolidayRoutes.New())

    simpleHtml("Holidays") {
        field("Count", holidays.size)

        table {
            tr {
                th { +"Name" }
                th { +"Calendar" }
                th { +"Date" }
                th { +"Purpose" }
            }
            holidays.forEach { holiday ->
                tr {
                    tdLink(call, state, holiday)
                    tdLink(call, state, holiday.calendar)
                    td { +holiday.relativeDate.display(calendar) }
                    td { displayHolidayPurpose(call, state, holiday.purpose) }
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showHolidayDetails(
    call: ApplicationCall,
    state: State,
    holiday: Holiday,
) {
    val backLink = call.application.href(HolidayRoutes())
    val deleteLink = call.application.href(HolidayRoutes.Delete(holiday.id))
    val editLink = call.application.href(HolidayRoutes.Edit(holiday.id))

    simpleHtmlDetails(holiday) {
        showHoliday(call, state, holiday)

        action(editLink, "Edit")
        if (state.canDelete(holiday.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showHolidayEditor(
    call: ApplicationCall,
    state: State,
    holiday: Holiday,
) {
    val backLink = href(call, holiday.id)
    val previewLink = call.application.href(HolidayRoutes.Preview(holiday.id))
    val updateLink = call.application.href(HolidayRoutes.Update(holiday.id))

    simpleHtmlEditor(holiday) {
        formWithPreview(previewLink, updateLink, backLink) {
            editHoliday(state, holiday)
        }
    }
}

