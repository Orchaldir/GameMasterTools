package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.name.editNameList
import at.orchaldir.gm.app.html.util.name.parseNameList
import at.orchaldir.gm.app.html.util.name.showNameList
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.health.DiseaseRoutes
import at.orchaldir.gm.core.action.CreateNameList
import at.orchaldir.gm.core.action.DeleteDisease
import at.orchaldir.gm.core.action.DeleteNameList
import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.NAME_LIST_TYPE
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.name.NameListId
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$NAME_LIST_TYPE")
class NameListRoutes {
    @Resource("details")
    class Details(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("new")
    class New(val parent: NameListRoutes = NameListRoutes())

    @Resource("delete")
    class Delete(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("edit")
    class Edit(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("update")
    class Update(val id: NameListId, val parent: NameListRoutes = NameListRoutes())
}

fun Application.configureNameListRouting() {
    routing {
        get<NameListRoutes> {
            logger.info { "Get all name lists" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllNameLists(call, STORE.getState())
            }
        }
        get<NameListRoutes.Details> { details ->
            logger.info { "Get details of name list ${details.id.value}" }

            val state = STORE.getState()
            val nameList = state.getNameListStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showNameListDetails(call, state, nameList)
            }
        }
        get<NameListRoutes.New> {
            logger.info { "Add new name list" }

            STORE.dispatch(CreateNameList)

            call.respondRedirect(
                call.application.href(
                    NameListRoutes.Edit(
                        STORE.getState().getNameListStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<NameListRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteNameList(delete.id), NameListRoutes())
        }
        get<NameListRoutes.Edit> { edit ->
            logger.info { "Get editor for name list ${edit.id.value}" }

            val state = STORE.getState()
            val nameList = state.getNameListStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showNameListEditor(call, nameList)
            }
        }
        post<NameListRoutes.Update> { update ->
            logger.info { "Update name list ${update.id.value}" }

            val nameList = parseNameList(update.id, call.receiveParameters())

            STORE.dispatch(UpdateNameList(nameList))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllNameLists(
    call: ApplicationCall,
    state: State,
) {
    val nameLists = state.getNameListStorage().getAll().sortedBy { it.name.text }
    val createLink = call.application.href(NameListRoutes.New())

    simpleHtml("Name Lists") {
        field("Count", nameLists.size)

        table {
            tr {
                th { +"Name" }
                th { +"Count" }
            }
            nameLists.forEach { nameList ->
                tr {
                    tdLink(call, state, nameList)
                    tdSkipZero(nameList.names)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showNameListDetails(
    call: ApplicationCall,
    state: State,
    nameList: NameList,
) {
    val backLink = call.application.href(NameListRoutes())
    val deleteLink = call.application.href(NameListRoutes.Delete(nameList.id))
    val editLink = call.application.href(NameListRoutes.Edit(nameList.id))

    simpleHtmlDetails(nameList) {
        showNameList(call, state, nameList)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showNameListEditor(
    call: ApplicationCall,
    nameList: NameList,
) {
    val backLink = href(call, nameList.id)
    val updateLink = call.application.href(NameListRoutes.Update(nameList.id))

    simpleHtmlEditor(nameList) {
        form {
            editNameList(nameList)
            button("Update", updateLink)
        }
        back(backLink)
    }
}


