package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editPantheon
import at.orchaldir.gm.app.html.religion.parsePantheon
import at.orchaldir.gm.app.html.religion.showPantheon
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowAllElements
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.PANTHEON_TYPE
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.util.SortPantheon
import at.orchaldir.gm.core.selector.util.sortPantheons
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

@Resource("/$PANTHEON_TYPE")
class PantheonRoutes : Routes<PantheonId, SortPantheon> {
    @Resource("all")
    class All(
        val sort: SortPantheon = SortPantheon.Name,
        val parent: PantheonRoutes = PantheonRoutes(),
    )

    @Resource("details")
    class Details(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("new")
    class New(val parent: PantheonRoutes = PantheonRoutes())

    @Resource("delete")
    class Delete(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("edit")
    class Edit(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("preview")
    class Preview(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    @Resource("update")
    class Update(val id: PantheonId, val parent: PantheonRoutes = PantheonRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPantheon) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PantheonId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PantheonId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configurePantheonRouting() {
    routing {
        get<PantheonRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                PantheonRoutes(),
                state.sortPantheons(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    Column("Gods") { tdSkipZero(it.gods) },
                    Column("Believers") { tdBelievers(state.getCharacterStorage(), it.id) },
                    Column("Organization") { tdBelievers(state.getOrganizationStorage(), it.id) },
                ),
            )
        }
        get<PantheonRoutes.Details> { details ->
            handleShowElement(details.id, PantheonRoutes(), HtmlBlockTag::showPantheon)
        }
        get<PantheonRoutes.New> {
            handleCreateElement(STORE.getState().getPantheonStorage()) { id ->
                PantheonRoutes.Edit(id)
            }
        }
        get<PantheonRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PantheonRoutes.All())
        }
        get<PantheonRoutes.Edit> { edit ->
            logger.info { "Get editor for pantheon ${edit.id.value}" }

            val state = STORE.getState()
            val pantheon = state.getPantheonStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPantheonEditor(call, state, pantheon)
            }
        }
        post<PantheonRoutes.Preview> { preview ->
            logger.info { "Get preview for pantheon ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val pantheon = parsePantheon(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPantheonEditor(call, state, pantheon)
            }
        }
        post<PantheonRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePantheon)
        }
    }
}

private fun HTML.showPantheonEditor(
    call: ApplicationCall,
    state: State,
    pantheon: Pantheon,
) {
    val backLink = href(call, pantheon.id)
    val previewLink = call.application.href(PantheonRoutes.Preview(pantheon.id))
    val updateLink = call.application.href(PantheonRoutes.Update(pantheon.id))

    simpleHtmlEditor(pantheon) {
        formWithPreview(previewLink, updateLink, backLink) {
            editPantheon(state, pantheon)
        }
    }
}

