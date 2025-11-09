package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.item.periodical.editPeriodicalIssue
import at.orchaldir.gm.app.html.item.periodical.parsePeriodicalIssue
import at.orchaldir.gm.app.html.item.periodical.showPeriodicalIssue
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.item.periodical.PERIODICAL_ISSUE_TYPE
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssueId
import at.orchaldir.gm.core.model.util.SortPeriodicalIssue
import at.orchaldir.gm.core.selector.util.sortPeriodicalIssues
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$PERIODICAL_ISSUE_TYPE")
class PeriodicalIssueRoutes : Routes<PeriodicalIssueId, SortPeriodicalIssue> {
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
    override fun all(call: ApplicationCall, sort: SortPeriodicalIssue) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PeriodicalIssueId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PeriodicalIssueId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: PeriodicalIssueId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: PeriodicalIssueId) = call.application.href(Update(id))
}

fun Application.configurePeriodicalIssueRouting() {
    routing {
        get<PeriodicalIssueRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                PeriodicalIssueRoutes(),
                state.sortPeriodicalIssues(all.sort),
                listOf(
                    tdColumn("Date") { link(call, it.id, it.dateAsName(state)) },
                    Column("Periodical") { tdLink(call, state, it.periodical) },
                    countCollectionColumn("Articles") { it.articles }
                ),
            )
        }
        get<PeriodicalIssueRoutes.Details> { details ->
            handleShowElement(details.id, PeriodicalIssueRoutes(), HtmlBlockTag::showPeriodicalIssue)
        }
        get<PeriodicalIssueRoutes.New> {
            handleCreateElement(PeriodicalIssueRoutes(), STORE.getState().getPeriodicalIssueStorage())
        }
        get<PeriodicalIssueRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PeriodicalIssueRoutes.All())
        }
        get<PeriodicalIssueRoutes.Edit> { edit ->
            handleEditElement(edit.id, PeriodicalIssueRoutes(), HtmlBlockTag::editPeriodicalIssue)
        }
        post<PeriodicalIssueRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                PeriodicalIssueRoutes(),
                ::parsePeriodicalIssue,
                HtmlBlockTag::editPeriodicalIssue
            )
        }
        post<PeriodicalIssueRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePeriodicalIssue)
        }
    }
}
