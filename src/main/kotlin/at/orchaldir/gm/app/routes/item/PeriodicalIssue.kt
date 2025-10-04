package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.periodical.editPeriodicalIssue
import at.orchaldir.gm.app.html.item.periodical.parsePeriodicalIssue
import at.orchaldir.gm.app.html.item.periodical.showPeriodicalIssue
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.PERIODICAL_ISSUE_TYPE
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.model.util.SortPeriodicalIssue
import at.orchaldir.gm.core.selector.util.sortPeriodicalIssues
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

@Resource("/$PERIODICAL_ISSUE_TYPE")
class PeriodicalIssueRoutes : Routes<PeriodicalIssueId> {
    @Resource("all")
    class All(
        val sort: SortPeriodicalIssue = SortPeriodicalIssue.Date,
        val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes(),
    )

    @Resource("details")
    class Details(val id: PeriodicalIssueId, val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes())

    @Resource("new")
    class New(val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes())

    @Resource("delete")
    class Delete(val id: PeriodicalIssueId, val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes())

    @Resource("edit")
    class Edit(val id: PeriodicalIssueId, val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes())

    @Resource("preview")
    class Preview(val id: PeriodicalIssueId, val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes())

    @Resource("update")
    class Update(val id: PeriodicalIssueId, val parent: PeriodicalIssueRoutes = PeriodicalIssueRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: PeriodicalIssueId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PeriodicalIssueId) = call.application.href(Edit(id))
}

fun Application.configurePeriodicalIssueRouting() {
    routing {
        get<PeriodicalIssueRoutes.All> { all ->
            logger.info { "Get all periodical issues" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPeriodicalIssues(call, STORE.getState(), all.sort)
            }
        }
        get<PeriodicalIssueRoutes.Details> { details ->
            handleShowElement(details.id, PeriodicalIssueRoutes(), HtmlBlockTag::showPeriodicalIssue)
        }
        get<PeriodicalIssueRoutes.New> {
            handleCreateElement(STORE.getState().getPeriodicalIssueStorage()) { id ->
                PeriodicalIssueRoutes.Edit(id)
            }
        }
        get<PeriodicalIssueRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PeriodicalIssueRoutes.All())
        }
        get<PeriodicalIssueRoutes.Edit> { edit ->
            logger.info { "Get editor for periodical issues ${edit.id.value}" }

            val state = STORE.getState()
            val issue = state.getPeriodicalIssueStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPeriodicalIssueEditor(call, state, issue)
            }
        }
        post<PeriodicalIssueRoutes.Preview> { preview ->
            logger.info { "Preview periodical issues ${preview.id.value}" }

            val state = STORE.getState()
            val issue = parsePeriodicalIssue(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPeriodicalIssueEditor(call, state, issue)
            }
        }
        post<PeriodicalIssueRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePeriodicalIssue)
        }
    }
}

private fun HTML.showAllPeriodicalIssues(
    call: ApplicationCall,
    state: State,
    sort: SortPeriodicalIssue,
) {
    val periodicals = state.sortPeriodicalIssues(sort)
    val createLink = call.application.href(PeriodicalIssueRoutes.New())
    call.application.href(PeriodicalIssueRoutes.All(SortPeriodicalIssue.Date))
    call.application.href(PeriodicalIssueRoutes.All(SortPeriodicalIssue.Periodical))

    simpleHtml("Periodical Issues") {
        field("Count", periodicals.size)
        showSortTableLinks(call, SortPeriodicalIssue.entries, PeriodicalIssueRoutes(), PeriodicalIssueRoutes::All)
        table {
            tr {
                th { +"Date" }
                th { +"Periodical" }
            }
            periodicals.forEach { issue ->
                tr {
                    td { link(call, issue.id, issue.dateAsName(state)) }
                    tdLink(call, state, issue.periodical)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showPeriodicalIssueEditor(
    call: ApplicationCall,
    state: State,
    periodical: PeriodicalIssue,
) {
    val backLink = href(call, periodical.id)
    val previewLink = call.application.href(PeriodicalIssueRoutes.Preview(periodical.id))
    val updateLink = call.application.href(PeriodicalIssueRoutes.Update(periodical.id))

    simpleHtml("Edit PeriodicalIssue: ${periodical.name(state)}") {
        formWithPreview(previewLink, updateLink, backLink) {
            editPeriodicalIssue(state, periodical)
        }
    }
}
