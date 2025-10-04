package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.showDeleteResult
import at.orchaldir.gm.app.html.simpleHtmlDetails
import at.orchaldir.gm.app.html.split
import at.orchaldir.gm.core.action.CloneAction
import at.orchaldir.gm.core.action.CreateAction
import at.orchaldir.gm.core.action.DeleteAction
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.CannotDeleteException
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.call
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag

interface Routes<ID : Id<ID>> {

    fun all(call: ApplicationCall): String
    fun clone(call: ApplicationCall, id: ID): String? = null
    fun delete(call: ApplicationCall, id: ID): String
    fun edit(call: ApplicationCall, id: ID): String
}

suspend inline fun <reified T : Any, ID : Id<ID>, ELEMENT : Element<ID>> PipelineContext<Unit, ApplicationCall>.handleCreateElement(
    storage: Storage<ID, ELEMENT>,
    createResource: (ID) -> T,
) {
    val id = storage.nextId
    logger.info { "Create ${id.print()}" }

    STORE.dispatch(CreateAction(id))

    val resource = createResource(id)
    call.respondRedirect(call.application.href(resource))

    STORE.getState().save()
}

suspend inline fun <reified T : Any, ID : Id<ID>, ELEMENT : Element<ID>> PipelineContext<Unit, ApplicationCall>.handleCloneElement(
    id: ID,
    createResource: (ID) -> T,
) {
    logger.info { "Clone ${id.print()}" }

    STORE.dispatch(CloneAction(id))

    val storage = STORE.getState().getStorage<ID, ELEMENT>(id)
    val resource = createResource(storage.lastId)
    call.respondRedirect(call.application.href(resource))

    STORE.getState().save()
}

suspend inline fun <reified T : Any, ID : Id<ID>> PipelineContext<Unit, ApplicationCall>.handleDeleteElement(
    id: ID,
    routes: T,
) {
    logger.info { "Delete ${id.print()}" }

    try {
        STORE.dispatch(DeleteAction(id))

        call.respondRedirect(call.application.href(routes))

        STORE.getState().save()
    } catch (e: CannotDeleteException) {
        logger.warn { e.message }
        call.respondHtml(HttpStatusCode.OK) {
            showDeleteResult(call, STORE.getState(), e.result)
        }
    }
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>> PipelineContext<Unit, ApplicationCall>.handleShowElement(
    id: ID,
    routes: Routes<ID>,
    noinline showDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Get details of ${id.print()}" }

    val state = STORE.getState()
    val storage = state.getStorage<ID, ELEMENT>(id)
    val element = storage.getOrThrow(id)

    call.respondHtml(HttpStatusCode.OK) {
        showElementDetails(call, state, element, routes, showDetails)
    }
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>> PipelineContext<Unit, ApplicationCall>.handleShowElementSplit(
    id: ID,
    routes: Routes<ID>,
    noinline showLeft: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    noinline showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Get details of ${id.print()}" }

    val state = STORE.getState()
    val storage = state.getStorage<ID, ELEMENT>(id)
    val element = storage.getOrThrow(id)

    call.respondHtml(HttpStatusCode.OK) {
        showElementDetailsSplit(call, state, element, routes, showLeft, showRight)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HTML.showElementDetails(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
    routes: Routes<ID>,
    showDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    val backLink = routes.all(call)
    val cloneLink = routes.clone(call, element.id())
    val deleteLink = routes.delete(call, element.id())
    val editLink = routes.edit(call, element.id())

    simpleHtmlDetails(state, element) {
        showDetails(call, state, element)

        if (cloneLink != null) {
            action(cloneLink, "Clone")
        }
        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HTML.showElementDetailsSplit(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
    routes: Routes<ID>,
    showLeft: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    val backLink = routes.all(call)
    val cloneLink = routes.clone(call, element.id())
    val deleteLink = routes.delete(call, element.id())
    val editLink = routes.edit(call, element.id())

    simpleHtmlDetails(state, element) {
        split({
            showLeft(call, state, element)

            if (cloneLink != null) {
                action(cloneLink, "Clone")
            }
            action(editLink, "Edit")
            action(deleteLink, "Delete")
            back(backLink)
        }, {
            showRight(call, state, element)
        })
    }
}

suspend fun <ID : Id<ID>, ELEMENT : Element<ID>> PipelineContext<Unit, ApplicationCall>.handleUpdateElement(
    id: ID,
    parse: (State, Parameters, ID) -> ELEMENT,
) {
    val parameters = call.receiveParameters()
    handleUpdateElement(id, { state, id -> parse(state, parameters, id) })
}

suspend fun <ID : Id<ID>, ELEMENT : Element<ID>> PipelineContext<Unit, ApplicationCall>.handleUpdateElement(
    id: ID,
    parse: (State, ID) -> ELEMENT,
    text: String = "Update",
) {
    logger.info { "$text ${id.print()}" }

    val element = parse(STORE.getState(), id)
    STORE.dispatch(UpdateAction(element))

    call.respondRedirect(href(call, id))

    STORE.getState().save()
}