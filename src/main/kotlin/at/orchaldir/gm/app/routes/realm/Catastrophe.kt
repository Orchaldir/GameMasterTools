package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.displayCauseOfCatastrophe
import at.orchaldir.gm.app.html.realm.editCatastrophe
import at.orchaldir.gm.app.html.realm.parseCatastrophe
import at.orchaldir.gm.app.html.realm.showCatastrophe
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.tdDestroyed
import at.orchaldir.gm.app.html.util.thDestroyed
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CATASTROPHE_TYPE
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.util.SortCatastrophe
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortCatastrophes
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

@Resource("/$CATASTROPHE_TYPE")
class CatastropheRoutes {
    @Resource("all")
    class All(
        val sort: SortCatastrophe = SortCatastrophe.Name,
        val parent: CatastropheRoutes = CatastropheRoutes(),
    )

    @Resource("details")
    class Details(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("new")
    class New(val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("delete")
    class Delete(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("edit")
    class Edit(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("preview")
    class Preview(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("update")
    class Update(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())
}

fun Application.configureCatastropheRouting() {
    routing {
        get<CatastropheRoutes.All> { all ->
            logger.info { "Get all catastrophes" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCatastrophes(call, STORE.getState(), all.sort)
            }
        }
        get<CatastropheRoutes.Details> { details ->
            logger.info { "Get details of catastrophe ${details.id.value}" }

            val state = STORE.getState()
            val catastrophe = state.getCatastropheStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCatastropheDetails(call, state, catastrophe)
            }
        }
        get<CatastropheRoutes.New> {
            handleCreateElement(STORE.getState().getCatastropheStorage()) { id ->
                CatastropheRoutes.Edit(id)
            }
        }
        get<CatastropheRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CatastropheRoutes.All())
        }
        get<CatastropheRoutes.Edit> { edit ->
            logger.info { "Get editor for catastrophe ${edit.id.value}" }

            val state = STORE.getState()
            val catastrophe = state.getCatastropheStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCatastropheEditor(call, state, catastrophe)
            }
        }
        post<CatastropheRoutes.Preview> { preview ->
            logger.info { "Get preview for catastrophe ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val catastrophe = parseCatastrophe(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCatastropheEditor(call, state, catastrophe)
            }
        }
        post<CatastropheRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCatastrophe)
        }
    }
}

private fun HTML.showAllCatastrophes(
    call: ApplicationCall,
    state: State,
    sort: SortCatastrophe,
) {
    val calendar = state.getDefaultCalendar()
    val catastrophes = state.sortCatastrophes(sort)
    val createLink = call.application.href(CatastropheRoutes.New())

    simpleHtml("Catastrophes") {
        field("Count", catastrophes.size)
        showSortTableLinks(call, SortCatastrophe.entries, CatastropheRoutes(), CatastropheRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"End" }
                th { +"Years" }
                th { +"Cause" }
                thDestroyed()
            }
            catastrophes.forEach { catastrophe ->
                tr {
                    tdLink(call, state, catastrophe)
                    td { showOptionalDate(call, state, catastrophe.startDate) }
                    td { showOptionalDate(call, state, catastrophe.endDate) }
                    tdSkipZero(calendar.getYears(catastrophe.getDuration(state)))
                    td { displayCauseOfCatastrophe(call, state, catastrophe.cause, false) }
                    tdDestroyed(state, catastrophe.id)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCatastropheDetails(
    call: ApplicationCall,
    state: State,
    catastrophe: Catastrophe,
) {
    val backLink = call.application.href(CatastropheRoutes.All())
    val deleteLink = call.application.href(CatastropheRoutes.Delete(catastrophe.id))
    val editLink = call.application.href(CatastropheRoutes.Edit(catastrophe.id))

    simpleHtmlDetails(catastrophe) {
        showCatastrophe(call, state, catastrophe)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showCatastropheEditor(
    call: ApplicationCall,
    state: State,
    catastrophe: Catastrophe,
) {
    val backLink = href(call, catastrophe.id)
    val previewLink = call.application.href(CatastropheRoutes.Preview(catastrophe.id))
    val updateLink = call.application.href(CatastropheRoutes.Update(catastrophe.id))

    simpleHtmlEditor(catastrophe) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCatastrophe(state, catastrophe)
        }
    }
}
