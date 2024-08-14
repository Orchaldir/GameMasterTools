package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.NAME
import at.orchaldir.gm.app.parse.parseNameList
import at.orchaldir.gm.core.action.CreateNameList
import at.orchaldir.gm.core.action.DeleteNameList
import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCultures
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

@Resource("/names")
class NameLists {
    @Resource("details")
    class Details(val id: NameListId, val parent: NameLists = NameLists())

    @Resource("new")
    class New(val parent: NameLists = NameLists())

    @Resource("delete")
    class Delete(val id: NameListId, val parent: NameLists = NameLists())

    @Resource("edit")
    class Edit(val id: NameListId, val parent: NameLists = NameLists())

    @Resource("update")
    class Update(val id: NameListId, val parent: NameLists = NameLists())
}

fun Application.configureNameListRouting() {
    routing {
        get<NameLists> {
            logger.info { "Get all name lists" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllNameLists(call)
            }
        }
        get<NameLists.Details> { details ->
            logger.info { "Get details of name list ${details.id.value}" }

            val state = STORE.getState()
            val nameList = state.getNameListStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showNameListDetails(call, state, nameList)
            }
        }
        get<NameLists.New> {
            logger.info { "Add new name list" }

            STORE.dispatch(CreateNameList)

            call.respondRedirect(call.application.href(NameLists.Edit(STORE.getState().getNameListStorage().lastId)))

            STORE.getState().save()
        }
        get<NameLists.Delete> { delete ->
            logger.info { "Delete name list ${delete.id.value}" }

            STORE.dispatch(DeleteNameList(delete.id))

            call.respondRedirect(call.application.href(NameLists()))

            STORE.getState().save()
        }
        get<NameLists.Edit> { edit ->
            logger.info { "Get editor for name list ${edit.id.value}" }

            val state = STORE.getState()
            val nameList = state.getNameListStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showNameListEditor(call, nameList)
            }
        }
        post<NameLists.Update> { update ->
            logger.info { "Update name list ${update.id.value}" }

            val nameList = parseNameList(update.id, call.receiveParameters())

            STORE.dispatch(UpdateNameList(nameList))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllNameLists(call: ApplicationCall) {
    val nameLists = STORE.getState().getNameListStorage().getAll().sortedBy { it.name }
    val count = nameLists.size
    val createLink = call.application.href(NameLists.New())

    simpleHtml("Name Lists") {
        field("Count", count.toString())
        showList(nameLists) { nameList ->
            link(call, nameList)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showNameListDetails(
    call: ApplicationCall,
    state: State,
    nameList: NameList,
) {
    val backLink = call.application.href(NameLists())
    val deleteLink = call.application.href(NameLists.Delete(nameList.id))
    val editLink = call.application.href(NameLists.Edit(nameList.id))

    simpleHtml("Name List: ${nameList.name}") {
        field("Id", nameList.id.value.toString())
        field("Name", nameList.name)
        showList("Names", nameList.names) { name ->
            +name
        }
        showList("Cultures", state.getCultures(nameList.id)) { culture ->
            link(call, culture)
        }
        p { a(editLink) { +"Edit" } }
        if (state.canDelete(nameList.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showNameListEditor(
    call: ApplicationCall,
    nameList: NameList,
) {
    val backLink = href(call, nameList.id)
    val updateLink = call.application.href(NameLists.Update(nameList.id))

    simpleHtml("Edit Name List: ${nameList.name}") {
        field("Id", nameList.id.value.toString())
        form {
            field("Name") {
                textInput(name = NAME) {
                    value = nameList.name
                }
            }
            h2 { +"Names" }
            textArea {
                id = "names"
                name = "names"
                cols = "30"
                rows = (nameList.name.length + 5).toString()
                +nameList.names.joinToString("\n")
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}
