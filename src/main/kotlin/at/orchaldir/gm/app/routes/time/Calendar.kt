package at.orchaldir.gm.app.routes.time

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.rpg.statistic.editStatistic
import at.orchaldir.gm.app.html.rpg.statistic.parseStatistic
import at.orchaldir.gm.app.html.time.editCalendar
import at.orchaldir.gm.app.html.time.parseCalendar
import at.orchaldir.gm.app.html.time.showCalendar
import at.orchaldir.gm.app.html.util.showDate
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.rpg.StatisticRoutes
import at.orchaldir.gm.app.routes.rpg.StatisticRoutes.Edit
import at.orchaldir.gm.app.routes.rpg.StatisticRoutes.Preview
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.util.SortCalendar
import at.orchaldir.gm.core.selector.time.date.convertDate
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortCalendars
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

@Resource("/$CALENDAR_TYPE")
class CalendarRoutes : Routes<CalendarId, SortCalendar> {
    @Resource("all")
    class All(
        val sort: SortCalendar = SortCalendar.Name,
        val parent: CalendarRoutes = CalendarRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCalendar) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: CalendarId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CalendarId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CalendarId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CalendarId) = call.application.href(Edit(id))
}

fun Application.configureCalendarRouting() {
    routing {
        get<CalendarRoutes.All> { all ->
            val state = STORE.getState()
            val defaultCalendar = state.getDefaultCalendar()

            handleShowAllElements(
                CalendarRoutes(),
                state.sortCalendars(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Default") {
                        if (it == defaultCalendar) {
                            +"yes"
                        }
                    },
                    Column("Days") { tdSkipZero(it.getDaysPerYear()) },
                    Column("Months") { tdSkipZero(it.getMonthsPerYear()) },
                    tdColumn("Start in Default") { showDate(call, state, it.getStartDateInDefaultCalendar()) },
                    tdColumn("Today") {
                        val today = convertDate(defaultCalendar, it, state.getCurrentDate())
                        showDate(call, state, it, today)
                    },
                ),
            )
        }
        get<CalendarRoutes.Details> { details ->
            handleShowElement(details.id, CalendarRoutes(), HtmlBlockTag::showCalendar)
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
            handleEditElement(edit.id, CalendarRoutes(), HtmlBlockTag::editCalendar)
        }
        post<CalendarRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, CalendarRoutes(), ::parseCalendar, HtmlBlockTag::editCalendar)
        }
        post<CalendarRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCalendar)
        }
    }
}
