package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.time.displayHolidayPurpose
import at.orchaldir.gm.app.html.time.editHoliday
import at.orchaldir.gm.app.html.time.parseHoliday
import at.orchaldir.gm.app.html.time.showHoliday
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.holiday.HOLIDAY_TYPE
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.SortHoliday
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortHolidays
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

@Resource("/$HOLIDAY_TYPE")
class HolidayRoutes : Routes<HolidayId,SortHoliday> {
    @Resource("all")
    class All(
        val sort: SortHoliday = SortHoliday.Name,
        val parent: HolidayRoutes = HolidayRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortHoliday) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: HolidayId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: HolidayId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureHolidayRouting() {
    routing {
        get<HolidayRoutes.All> { all ->
            logger.info { "Get all holidays" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllHolidays(call, STORE.getState(), all.sort)
            }
        }
        get<HolidayRoutes.Details> { details ->
            handleShowElement(details.id, HolidayRoutes(), HtmlBlockTag::showHoliday)
        }
        get<HolidayRoutes.New> {
            handleCreateElement(STORE.getState().getHolidayStorage()) { id ->
                HolidayRoutes.Edit(id)
            }
        }
        get<HolidayRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, HolidayRoutes())
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

            val state = STORE.getState()
            val holiday = parseHoliday(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showHolidayEditor(call, state, holiday)
            }
        }
        post<HolidayRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseHoliday)
        }
    }
}

private fun HTML.showAllHolidays(
    call: ApplicationCall,
    state: State,
    sort: SortHoliday,
) {
    val holidays = state.sortHolidays(sort)
    val calendar = state.getDefaultCalendar()
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

