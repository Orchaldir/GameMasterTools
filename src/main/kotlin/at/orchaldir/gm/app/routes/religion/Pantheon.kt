package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editPantheon
import at.orchaldir.gm.app.html.religion.parsePantheon
import at.orchaldir.gm.app.html.religion.showPantheon
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.DeletePantheon
import at.orchaldir.gm.core.action.UpdatePantheon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.PANTHEON_TYPE
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.util.SortPantheon
import at.orchaldir.gm.core.selector.util.getBelievers
import at.orchaldir.gm.core.selector.util.sortPantheons
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
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$PANTHEON_TYPE")
class PantheonRoutes {
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
}

fun Application.configurePantheonRouting() {
    routing {
        get<PantheonRoutes.All> { all ->
            logger.info { "Get all pantheons" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPantheons(call, STORE.getState(), all.sort)
            }
        }
        get<PantheonRoutes.Details> { details ->
            logger.info { "Get details of pantheon ${details.id.value}" }

            val state = STORE.getState()
            val pantheon = state.getPantheonStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPantheonDetails(call, state, pantheon)
            }
        }
        get<PantheonRoutes.New> {
            handleCreateElement(STORE.getState().getPantheonStorage()) { id ->
                PantheonRoutes.Edit(id)
            }
        }
        get<PantheonRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeletePantheon(delete.id), PantheonRoutes())
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
            val pantheon = parsePantheon(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPantheonEditor(call, state, pantheon)
            }
        }
        post<PantheonRoutes.Update> { update ->
            logger.info { "Update pantheon ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val pantheon = parsePantheon(formParameters, update.id)

            STORE.dispatch(UpdatePantheon(pantheon))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllPantheons(
    call: ApplicationCall,
    state: State,
    sort: SortPantheon,
) {
    val pantheons = state.sortPantheons(sort)
    val createLink = call.application.href(PantheonRoutes.New())

    simpleHtml("Pantheons") {
        field("Count", pantheons.size)
        showSortTableLinks(call, SortPantheon.entries, PantheonRoutes(), PantheonRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Gods" }
                th { +"Believers" }
                th { +"Organizations" }
            }
            pantheons.forEach { pantheon ->
                tr {
                    tdLink(call, state, pantheon)
                    tdString(pantheon.title)
                    tdSkipZero(pantheon.gods)
                    tdSkipZero(getBelievers(state.getCharacterStorage(), pantheon.id))
                    tdSkipZero(getBelievers(state.getOrganizationStorage(), pantheon.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showPantheonDetails(
    call: ApplicationCall,
    state: State,
    pantheon: Pantheon,
) {
    val backLink = call.application.href(PantheonRoutes.All())
    val deleteLink = call.application.href(PantheonRoutes.Delete(pantheon.id))
    val editLink = call.application.href(PantheonRoutes.Edit(pantheon.id))

    simpleHtmlDetails(pantheon) {
        showPantheon(call, state, pantheon)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
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

