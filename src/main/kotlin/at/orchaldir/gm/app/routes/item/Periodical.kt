package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.periodical.editPeriodical
import at.orchaldir.gm.app.html.item.periodical.parsePeriodical
import at.orchaldir.gm.app.html.item.periodical.showPeriodical
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
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
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

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
                    createSkipZeroColumnForId("Issues", state::countPeriodicalIssues),
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
            handleUpdateElement(update.id, ::parsePeriodical)
        }
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
