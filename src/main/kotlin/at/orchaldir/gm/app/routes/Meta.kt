package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
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
import kotlinx.html.*

interface Routes<ID : Id<ID>, T> {

    fun all(call: ApplicationCall): String
    fun all(call: ApplicationCall, sort: T): String
    fun clone(call: ApplicationCall, id: ID): String? = null
    fun delete(call: ApplicationCall, id: ID): String
    fun edit(call: ApplicationCall, id: ID): String
    fun gallery(call: ApplicationCall): String? = null
    fun new(call: ApplicationCall): String
    fun preview(call: ApplicationCall, id: ID): String
    fun update(call: ApplicationCall, id: ID): String
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, reified T : Enum<T>> PipelineContext<Unit, ApplicationCall>.handleShowAllElements(
    routes: Routes<ID, T>,
    elements: List<ELEMENT>,
    columns: List<Column<ELEMENT>>,
    crossinline extraContent: HtmlBlockTag.(List<ELEMENT>) -> Unit = { },
) {
    logger.info { "Get all elements" }

    call.respondHtml(HttpStatusCode.OK) {
        showAllElements(call, routes, elements, columns, extraContent)
    }
}

inline fun <ID : Id<ID>, ELEMENT : Element<ID>, reified T : Enum<T>> HTML.showAllElements(
    call: ApplicationCall,
    routes: Routes<ID, T>,
    elements: List<ELEMENT>,
    columns: List<Column<ELEMENT>>,
    crossinline extraContent: HtmlBlockTag.(List<ELEMENT>) -> Unit = { },
) {
    simpleHtml(elements.firstOrNull()?.id()?.plural() ?: "Elements") {
        field("Count", elements.size)
        showSortTableLinks(call, enumValues<T>().toList(), routes)
        routes.gallery(call)?.let { action(it, "Gallery") }

        table {
            tr {
                columns.forEach { (header, width, _) ->
                    thMultiLines(header, width)
                }
            }
            elements.forEach { element ->
                tr {
                    columns.forEach { (_, _, selectValue) ->
                        selectValue(element)
                    }
                }
            }
        }

        extraContent(elements)

        action(routes.new(call), "Add")
        back("/")
    }
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

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handleEditElement(
    id: ID,
    routes: Routes<ID, T>,
    noinline editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Edit ${id.print()}" }

    val state = STORE.getState()
    val storage = state.getStorage<ID, ELEMENT>(id)
    val element = storage.getOrThrow(id)

    showEditor(routes, state, element, editDetails)
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handleEditElementSplit(
    id: ID,
    routes: Routes<ID, T>,
    noinline editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    noinline showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Edit ${id.print()}" }

    val state = STORE.getState()
    val storage = state.getStorage<ID, ELEMENT>(id)
    val element = storage.getOrThrow(id)

    showSplitEditor(routes, state, element, editDetails, showRight)
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handlePreviewElement(
    id: ID,
    routes: Routes<ID, T>,
    parse: (State, Parameters, ID) -> ELEMENT,
    noinline editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Preview ${id.print()}" }

    val state = STORE.getState()
    val parameters = call.receiveParameters()
    val element = parse(state, parameters, id)

    showEditor(routes, state, element, editDetails)
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handlePreviewElementSplit(
    id: ID,
    routes: Routes<ID, T>,
    parse: (State, Parameters, ID) -> ELEMENT,
    noinline editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    noinline showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) = handlePreviewElementSplit(
    id,
    call.receiveParameters(),
    routes,
    parse,
    editDetails,
    showRight,
)

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handlePreviewElementSplit(
    id: ID,
    parameters: Parameters,
    routes: Routes<ID, T>,
    parse: (State, Parameters, ID) -> ELEMENT,
    noinline editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    noinline showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Preview ${id.print()}" }

    val state = STORE.getState()
    val element = parse(state, parameters, id)

    showSplitEditor(routes, state, element, editDetails, showRight)
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handleShowElement(
    id: ID,
    routes: Routes<ID, T>,
    noinline showDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Show ${id.print()}" }

    val state = STORE.getState()
    val storage = state.getStorage<ID, ELEMENT>(id)
    val element = storage.getOrThrow(id)

    call.respondHtml(HttpStatusCode.OK) {
        showElementDetails(call, state, element, routes, showDetails)
    }
}

suspend inline fun <ID : Id<ID>, ELEMENT : Element<ID>, T> PipelineContext<Unit, ApplicationCall>.handleShowElementSplit(
    id: ID,
    routes: Routes<ID, T>,
    noinline showLeft: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    noinline showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    logger.info { "Show ${id.print()}" }

    val state = STORE.getState()
    val storage = state.getStorage<ID, ELEMENT>(id)
    val element = storage.getOrThrow(id)

    call.respondHtml(HttpStatusCode.OK) {
        showElementDetailsSplit(call, state, element, routes, showLeft, showRight)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, T> HTML.showElementDetails(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
    routes: Routes<ID, T>,
    showDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    simpleHtmlDetails(state, element) {
        showDetails(call, state, element)
        showDetailsActions(call, routes, element)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, T> HTML.showElementDetailsSplit(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
    routes: Routes<ID, T>,
    showLeft: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    simpleHtmlDetails(state, element) {
        split({
            showLeft(call, state, element)
            showDetailsActions(call, routes, element)
        }, {
            showRight(call, state, element)
        })
    }
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>, T> HtmlBlockTag.showDetailsActions(
    call: ApplicationCall,
    routes: Routes<ID, T>,
    element: ELEMENT,
) {
    val backLink = routes.all(call)
    val cloneLink = routes.clone(call, element.id())
    val deleteLink = routes.delete(call, element.id())
    val editLink = routes.edit(call, element.id())

    h2 { +"Actions" }

    if (cloneLink != null) {
        action(cloneLink, "Clone")
    }
    action(editLink, "Edit")
    action(deleteLink, "Delete")
    back(backLink)
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

suspend fun <ELEMENT : Element<ID>, ID : Id<ID>, T> PipelineContext<Unit, ApplicationCall>.showEditor(
    routes: Routes<ID, T>,
    state: State,
    element: ELEMENT,
    editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    val backLink = href(call, element.id())
    val previewLink = routes.preview(call, element.id())
    val updateLink = routes.update(call, element.id())

    call.respondHtml(HttpStatusCode.OK) {
        simpleHtml(state, element, "Edit ", true) {
            mainFrame {
                formWithPreview(previewLink, updateLink, backLink) {
                    editDetails(call, state, element)
                }
            }
        }
    }
}

suspend fun <ELEMENT : Element<ID>, ID : Id<ID>, T> PipelineContext<Unit, ApplicationCall>.showSplitEditor(
    routes: Routes<ID, T>,
    state: State,
    element: ELEMENT,
    editDetails: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
    showRight: HtmlBlockTag.(ApplicationCall, State, ELEMENT) -> Unit,
) {
    val backLink = href(call, element.id())
    val previewLink = routes.preview(call, element.id())
    val updateLink = routes.update(call, element.id())

    call.respondHtml(HttpStatusCode.OK) {
        simpleHtml(state, element, "Edit ", true) {
            split({
                formWithPreview(previewLink, updateLink, backLink) {
                    editDetails(call, state, element)
                }
            }, {
                showRight(call, state, element)
            })
        }
    }
}