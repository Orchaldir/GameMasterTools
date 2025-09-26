package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.statistic.displayBaseValue
import at.orchaldir.gm.app.html.character.statistic.editStatistic
import at.orchaldir.gm.app.html.character.statistic.parseStatistic
import at.orchaldir.gm.app.html.character.statistic.showStatistic
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CreateStatistic
import at.orchaldir.gm.core.action.DeleteStatistic
import at.orchaldir.gm.core.action.UpdateStatistic
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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$STATISTIC_TYPE")
class StatisticRoutes {
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
}

fun Application.configureStatisticRouting() {
    routing {
        get<StatisticRoutes.All> { all ->
            logger.info { "Get all statistics" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllStatistics(call, STORE.getState(), all.sort)
            }
        }
        get<StatisticRoutes.Details> { details ->
            logger.info { "Get details of statistic ${details.id.value}" }

            val state = STORE.getState()
            val statistic = state.getStatisticStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStatisticDetails(call, state, statistic)
            }
        }
        get<StatisticRoutes.New> {
            logger.info { "Add new statistic" }

            STORE.dispatch(CreateStatistic)

            call.respondRedirect(
                call.application.href(
                    StatisticRoutes.Edit(
                        STORE.getState().getStatisticStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<StatisticRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteStatistic(delete.id), StatisticRoutes())
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
            val statistic = parseStatistic(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStatisticEditor(call, state, statistic)
            }
        }
        post<StatisticRoutes.Update> { update ->
            logger.info { "Update statistic ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val statistic = parseStatistic(formParameters, update.id)

            STORE.dispatch(UpdateStatistic(statistic))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllStatistics(
    call: ApplicationCall,
    state: State,
    sort: SortStatistic,
) {
    val statistics = state.sortStatistics(sort)
    val createLink = call.application.href(StatisticRoutes.New())

    simpleHtml("Statistics") {
        field("Count", statistics.size)
        showSortTableLinks(call, SortStatistic.entries, StatisticRoutes(), StatisticRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Short" }
                th { +"Type" }
                th { +"Base Value" }
            }
            statistics.forEach { statistic ->
                tr {
                    tdLink(call, state, statistic)
                    tdString(statistic.short)
                    tdEnum(statistic.data.getType())
                    td { displayBaseValue(call, state, statistic.data.baseValue()) }
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showStatisticDetails(
    call: ApplicationCall,
    state: State,
    statistic: Statistic,
) {
    val backLink = call.application.href(StatisticRoutes.All())
    val deleteLink = call.application.href(StatisticRoutes.Delete(statistic.id))
    val editLink = call.application.href(StatisticRoutes.Edit(statistic.id))

    simpleHtmlDetails(statistic) {
        showStatistic(call, state, statistic)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
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

    simpleHtmlEditor(statistic) {
        formWithPreview(previewLink, updateLink, backLink) {
            editStatistic(state, statistic)
        }
    }
}

