package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.character.statistic.*
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.STATISTIC_TYPE
import at.orchaldir.gm.core.model.character.statistic.Statistic
import at.orchaldir.gm.core.model.character.statistic.StatisticId
import at.orchaldir.gm.core.model.util.SortStatistic
import at.orchaldir.gm.core.selector.util.sortStatistics
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

private val logger = KotlinLogging.logger {}

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
            logger.info { "Get editor for statistic ${edit.id.value}" }

            val state = STORE.getState()
            val statistic = state.getStatisticStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStatisticEditor(call, state, statistic)
            }
        }
        post<StatisticRoutes.Preview> { preview ->
            logger.info { "Get preview for statistic ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val statistic = parseStatistic(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStatisticEditor(call, state, statistic)
            }
        }
        post<StatisticRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseStatistic)
        }
    }
}

private fun HTML.showStatisticEditor(
    call: ApplicationCall,
    state: State,
    statistic: Statistic,
) {
    val backLink = href(call, statistic.id)
    val previewLink = call.application.href(StatisticRoutes.Preview(statistic.id))
    val updateLink = call.application.href(StatisticRoutes.Update(statistic.id))

    simpleHtmlEditor(statistic, true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editStatistic(state, statistic)
            }
        }
    }
}

