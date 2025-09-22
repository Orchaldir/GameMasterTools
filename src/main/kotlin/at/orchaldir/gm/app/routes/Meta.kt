package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.showDeleteResult
import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.action.CloneAction
import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.CannotDeleteException
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

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

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.handleDeleteElement(
    id: Id<*>,
    action: Action,
    routes: T,
) {
    logger.info { "Delete ${id.print()}" }

    try {
        STORE.dispatch(action)

        call.respondRedirect(call.application.href(routes))

        STORE.getState().save()
    } catch (e: CannotDeleteException) {
        logger.warn { e.message }
        call.respondHtml(HttpStatusCode.OK) {
            showDeleteResult(call, STORE.getState(), e.result)
        }
    }
}