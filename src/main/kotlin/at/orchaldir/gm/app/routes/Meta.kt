package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.showDeleteResult
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
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

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