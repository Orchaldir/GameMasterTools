package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.periodical.editPeriodical
import at.orchaldir.gm.app.html.item.periodical.parsePeriodical
import at.orchaldir.gm.app.html.item.periodical.showPeriodical
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.item.periodical.PERIODICAL_TYPE
import at.orchaldir.gm.core.model.item.periodical.PeriodicalId
import at.orchaldir.gm.core.model.util.SortPeriodical
import at.orchaldir.gm.core.selector.item.periodical.countPeriodicalIssues
import at.orchaldir.gm.core.selector.util.sortPeriodicals
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$PERIODICAL_TYPE")
class PeriodicalRoutes : Routes<PeriodicalId, SortPeriodical> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPeriodical) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PeriodicalId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PeriodicalId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: PeriodicalId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: PeriodicalId) = call.application.href(Update(id))
}

fun Application.configurePeriodicalRouting() {
    routing {
        get<PeriodicalRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                PeriodicalRoutes(),
                state.sortPeriodicals(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createOwnerColumn(call, state),
                    Column("Language") { tdLink(call, state, it.language) },
                    Column("Frequency") { tdEnum(it.frequency) },
                    countColumnForId("Issues", state::countPeriodicalIssues),
                ),
            ) {
                showPeriodicalOwnershipCount(call, state, it)
                showPublicationFrequencies(it)
            }
        }
        get<PeriodicalRoutes.Details> { details ->
            handleShowElement(details.id, PeriodicalRoutes(), HtmlBlockTag::showPeriodical)
        }
        get<PeriodicalRoutes.New> {
            handleCreateElement(STORE.getState().getPeriodicalStorage()) { id ->
                PeriodicalRoutes.Edit(id)
            }
        }
        get<PeriodicalRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PeriodicalRoutes.All())
        }
        get<PeriodicalRoutes.Edit> { edit ->
            handleEditElement(edit.id, PeriodicalRoutes(), HtmlBlockTag::editPeriodical)
        }
        post<PeriodicalRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, PeriodicalRoutes(), ::parsePeriodical, HtmlBlockTag::editPeriodical)
        }
        post<PeriodicalRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePeriodical)
        }
    }
}
