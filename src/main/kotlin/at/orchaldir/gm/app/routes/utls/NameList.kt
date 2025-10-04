package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.name.editNameList
import at.orchaldir.gm.app.html.util.name.parseNameList
import at.orchaldir.gm.app.html.util.name.showNameList
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortNameList
import at.orchaldir.gm.core.model.util.name.NAME_LIST_TYPE
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.selector.util.sortNameLists
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$NAME_LIST_TYPE")
class NameListRoutes : Routes<NameListId> {
    @Resource("all")
    class All(
        val sort: SortNameList = SortNameList.Name,
        val parent: NameListRoutes = NameListRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: NameListId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: NameListId) = call.application.href(Edit(id))
}

fun Application.configureNameListRouting() {
    routing {
        get<NameListRoutes.All> { all ->
            logger.info { "Get all name lists" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllNameLists(call, STORE.getState(), all.sort)
            }
        }
        get<NameListRoutes.Details> { details ->
            handleShowElement(details.id, NameListRoutes(), HtmlBlockTag::showNameList)
        }
        get<NameListRoutes.New> {
            handleCreateElement(STORE.getState().getNameListStorage()) { id ->
                NameListRoutes.Edit(id)
            }
        }
        get<NameListRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, NameListRoutes())
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
            handleUpdateElement(update.id, ::parseNameList)
        }
    }
}

private fun HTML.showAllNameLists(
    call: ApplicationCall,
    state: State,
    sort: SortNameList,
) {
    val nameLists = state.sortNameLists(sort)
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


