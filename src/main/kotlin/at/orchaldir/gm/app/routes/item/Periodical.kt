package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.periodical.editPeriodical
import at.orchaldir.gm.app.html.item.periodical.parseArticle
import at.orchaldir.gm.app.html.item.periodical.parsePeriodical
import at.orchaldir.gm.app.html.item.periodical.showPeriodical
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.DeletePeriodical
import at.orchaldir.gm.core.action.UpdatePeriodical
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PERIODICAL_TYPE
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.util.SortPeriodical
import at.orchaldir.gm.core.selector.item.periodical.countPeriodicalIssues
import at.orchaldir.gm.core.selector.util.sortPeriodicals
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

@Resource("/$PERIODICAL_TYPE")
class PeriodicalRoutes {
    @Resource("all")
    class All(
        val sort: SortPeriodical = SortPeriodical.Name,
        val parent: PeriodicalRoutes = PeriodicalRoutes(),
    )

    @Resource("details")
    class Details(val id: PeriodicalId, val parent: PeriodicalRoutes = PeriodicalRoutes())

    @Resource("new")
    class New(val parent: PeriodicalRoutes = PeriodicalRoutes())

    @Resource("delete")
    class Delete(val id: PeriodicalId, val parent: PeriodicalRoutes = PeriodicalRoutes())

    @Resource("edit")
    class Edit(val id: PeriodicalId, val parent: PeriodicalRoutes = PeriodicalRoutes())

    @Resource("preview")
    class Preview(val id: PeriodicalId, val parent: PeriodicalRoutes = PeriodicalRoutes())

    @Resource("update")
    class Update(val id: PeriodicalId, val parent: PeriodicalRoutes = PeriodicalRoutes())
}

fun Application.configurePeriodicalRouting() {
    routing {
        get<PeriodicalRoutes.All> { all ->
            logger.info { "Get all periodical" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPeriodicals(call, STORE.getState(), all.sort)
            }
        }
        get<PeriodicalRoutes.Details> { details ->
            logger.info { "Get details of periodical ${details.id.value}" }

            val state = STORE.getState()
            val periodical = state.getPeriodicalStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPeriodicalDetails(call, state, periodical)
            }
        }
        get<PeriodicalRoutes.New> {
            handleCreateElement(STORE.getState().getPeriodicalStorage()) { id ->
                PeriodicalRoutes.Edit(id)
            }
        }
        get<PeriodicalRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeletePeriodical(delete.id), PeriodicalRoutes())
        }
        get<PeriodicalRoutes.Edit> { edit ->
            logger.info { "Get editor for periodical ${edit.id.value}" }

            val state = STORE.getState()
            val periodical = state.getPeriodicalStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPeriodicalEditor(call, state, periodical)
            }
        }
        post<PeriodicalRoutes.Preview> { preview ->
            logger.info { "Preview periodical ${preview.id.value}" }

            val state = STORE.getState()
            val periodical = parsePeriodical(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPeriodicalEditor(call, state, periodical)
            }
        }
        post<PeriodicalRoutes.Update> { update ->
            handleUpdateElement(parsePeriodical(STORE.getState(), call.receiveParameters(), update.id))
        }
    }
}

private fun HTML.showAllPeriodicals(
    call: ApplicationCall,
    state: State,
    sort: SortPeriodical,
) {
    val periodicals = state.sortPeriodicals(sort)
    val createLink = call.application.href(PeriodicalRoutes.New())

    simpleHtml("Periodicals") {
        field("Count", periodicals.size)
        showSortTableLinks(call, SortPeriodical.entries, PeriodicalRoutes(), PeriodicalRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"Owner" }
                th { +"Language" }
                th { +"Frequency" }
                th { +"Issues" }
            }
            periodicals.forEach { periodical ->
                tr {
                    tdLink(call, state, periodical)
                    td { showOptionalDate(call, state, periodical.calendar, periodical.startDate()) }
                    td { showReference(call, state, periodical.ownership.current, false) }
                    tdLink(call, state, periodical.language)
                    tdEnum(periodical.frequency)
                    tdSkipZero(state.countPeriodicalIssues(periodical.id))
                }
            }
        }
        showPeriodicalOwnershipCount(call, state, periodicals)
        showPublicationFrequencies(periodicals)
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showPeriodicalDetails(
    call: ApplicationCall,
    state: State,
    periodical: Periodical,
) {
    val backLink = call.application.href(PeriodicalRoutes.All())
    val deleteLink = call.application.href(PeriodicalRoutes.Delete(periodical.id))
    val editLink = call.application.href(PeriodicalRoutes.Edit(periodical.id))

    simpleHtmlDetails(periodical) {
        showPeriodical(call, state, periodical)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showPeriodicalEditor(
    call: ApplicationCall,
    state: State,
    periodical: Periodical,
) {
    val backLink = href(call, periodical.id)
    val previewLink = call.application.href(PeriodicalRoutes.Preview(periodical.id))
    val updateLink = call.application.href(PeriodicalRoutes.Update(periodical.id))

    simpleHtmlEditor(periodical) {
        formWithPreview(previewLink, updateLink, backLink) {
            editPeriodical(state, periodical)
        }
    }
}
