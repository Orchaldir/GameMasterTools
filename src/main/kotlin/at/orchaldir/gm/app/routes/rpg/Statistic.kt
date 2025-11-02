package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.statistic.*
import at.orchaldir.gm.app.html.tdEnum
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.statistic.STATISTIC_TYPE
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.util.SortStatistic
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$STATISTIC_TYPE")
class StatisticRoutes : Routes<StatisticId, SortStatistic> {
    @Resource("all")
    class All(
        val sort: SortStatistic = SortStatistic.Name,
        val parent: StatisticRoutes = StatisticRoutes(),
    )

    @Resource("details")
    class Details(val id: StatisticId, val parent: StatisticRoutes = StatisticRoutes())

    @Resource("new")
    class New(val parent: StatisticRoutes = StatisticRoutes())

    @Resource("delete")
    class Delete(val id: StatisticId, val parent: StatisticRoutes = StatisticRoutes())

    @Resource("edit")
    class Edit(val id: StatisticId, val parent: StatisticRoutes = StatisticRoutes())

    @Resource("preview")
    class Preview(val id: StatisticId, val parent: StatisticRoutes = StatisticRoutes())

    @Resource("update")
    class Update(val id: StatisticId, val parent: StatisticRoutes = StatisticRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortStatistic) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: StatisticId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: StatisticId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: StatisticId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: StatisticId) = call.application.href(Edit(id))
}

fun Application.configureStatisticRouting() {
    routing {
        get<StatisticRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                StatisticRoutes(),
                state.sortStatistics(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Short") { tdString(it.short) },
                    Column("Type") { tdEnum(it.data.getType()) },
                    tdColumn("Base Value") { displayBaseValue(call, state, it.data.baseValue()) },
                    tdColumn("Cost") { displayStatisticCost(it.data.cost(), false) },
                    tdColumn("Unit") { displayStatisticUnit(it.data, false) },
                ),
            )
        }
        get<StatisticRoutes.Details> { details ->
            handleShowElement(details.id, StatisticRoutes(), HtmlBlockTag::showStatistic)
        }
        get<StatisticRoutes.New> {
            handleCreateElement(STORE.getState().getStatisticStorage()) { id ->
                StatisticRoutes.Edit(id)
            }
        }
        get<StatisticRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, StatisticRoutes.All())
        }
        get<StatisticRoutes.Edit> { edit ->
            handleEditElement(edit.id, StatisticRoutes(), HtmlBlockTag::editStatistic)
        }
        post<StatisticRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, StatisticRoutes(), ::parseStatistic, HtmlBlockTag::editStatistic)
        }
        post<StatisticRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseStatistic)
        }
    }
}
