package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.time.displayHolidayPurpose
import at.orchaldir.gm.app.html.time.editHoliday
import at.orchaldir.gm.app.html.time.parseHoliday
import at.orchaldir.gm.app.html.time.showHoliday
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.time.holiday.HOLIDAY_TYPE
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.SortHoliday
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortHolidays
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$HOLIDAY_TYPE")
class HolidayRoutes : Routes<HolidayId, SortHoliday> {
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
    override fun preview(call: ApplicationCall, id: HolidayId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: HolidayId) = call.application.href(Update(id))
}

fun Application.configureHolidayRouting() {
    routing {
        get<HolidayRoutes.All> { all ->
            val state = STORE.getState()
            val calendar = state.getDefaultCalendar()

            handleShowAllElements(
                HolidayRoutes(),
                state.sortHolidays(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Calendar") { tdLink(call, state, it.calendar) },
                    tdColumn("Date") { +it.relativeDate.display(calendar) },
                    tdColumn("Purpose") { displayHolidayPurpose(call, state, it.purpose) },
                ),
            )
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
            handleEditElement(edit.id, HolidayRoutes(), HtmlBlockTag::editHoliday)
        }
        post<HolidayRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, HolidayRoutes(), ::parseHoliday, HtmlBlockTag::editHoliday)
        }
        post<HolidayRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseHoliday)
        }
    }
}
