package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseItem
import at.orchaldir.gm.core.action.CreateItem
import at.orchaldir.gm.core.action.DeleteItem
import at.orchaldir.gm.core.action.UpdateItem
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Item
import at.orchaldir.gm.core.model.item.ItemId
import at.orchaldir.gm.core.selector.canCreateItem
import at.orchaldir.gm.core.selector.canDelete
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

@Resource("/items")
class Items {
    @Resource("details")
    class Details(val id: ItemId, val parent: Items = Items())

    @Resource("new")
    class New(val parent: Items = Items())

    @Resource("delete")
    class Delete(val id: ItemId, val parent: Items = Items())

    @Resource("edit")
    class Edit(val id: ItemId, val parent: Items = Items())

    @Resource("update")
    class Update(val id: ItemId, val parent: Items = Items())
}

fun Application.configureItemRouting() {
    routing {
        get<Items> {
            logger.info { "Get all items" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllItems(call, STORE.getState())
            }
        }
        get<Items.Details> { details ->
            logger.info { "Get details of item ${details.id.value}" }

            val state = STORE.getState()
            val item = state.items.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemDetails(call, state, item)
            }
        }
        get<Items.New> {
            logger.info { "Add new item" }

            STORE.dispatch(CreateItem)

            call.respondRedirect(call.application.href(Items.Edit(STORE.getState().items.lastId)))

            STORE.getState().save()
        }
        get<Items.Delete> { delete ->
            logger.info { "Delete item ${delete.id.value}" }

            STORE.dispatch(DeleteItem(delete.id))

            call.respondRedirect(call.application.href(Items()))

            STORE.getState().save()
        }
        get<Items.Edit> { edit ->
            logger.info { "Get editor for item ${edit.id.value}" }

            val state = STORE.getState()
            val language = state.items.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemEditor(call, language)
            }
        }
        post<Items.Update> { update ->
            logger.info { "Update item ${update.id.value}" }

            val nameList = parseItem(update.id, call.receiveParameters())

            STORE.dispatch(UpdateItem(nameList))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllItems(
    call: ApplicationCall,
    state: State,
) {
    val templates = STORE.getState().items.getAll().sortedBy { it.name }
    val count = templates.size
    val createLink = call.application.href(Items.New(Items()))

    simpleHtml("Item Templates") {
        field("Count", count.toString())
        showList(templates) { nameList ->
            link(call, nameList)
        }
        if (state.canCreateItem()) {
            p { a(createLink) { +"Add" } }
        }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showItemDetails(
    call: ApplicationCall,
    state: State,
    item: Item,
) {
    val backLink = call.application.href(Items())
    val deleteLink = call.application.href(Items.Delete(item.id))
    val editLink = call.application.href(Items.Edit(item.id))

    simpleHtml("Name List: ${item.name}") {
        field("Id", item.id.value.toString())
        field("Name", item.name)
        p { a(editLink) { +"Edit" } }
        if (state.canDelete(item.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showItemEditor(
    call: ApplicationCall,
    nameList: Item,
) {
    val backLink = href(call, nameList.id)
    val updateLink = call.application.href(Items.Update(nameList.id))

    simpleHtml("Edit Name List: ${nameList.name}") {
        field("Id", nameList.id.value.toString())
        form {
            field("Name") {
                b { +"Name: " }
                textInput(name = "name") {
                    value = nameList.name
                }
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
