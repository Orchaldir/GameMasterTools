package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseItem
import at.orchaldir.gm.core.action.CreateItem
import at.orchaldir.gm.core.action.DeleteItem
import at.orchaldir.gm.core.action.UpdateItem
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getName
import at.orchaldir.gm.utils.doNothing
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
    class New(val template: ItemTemplateId, val parent: Items = Items())

    @Resource("delete")
    class Delete(val id: ItemId, val parent: Items = Items())

    @Resource("edit")
    class Edit(val id: ItemId, val parent: Items = Items())

    @Resource("update")
    class Update(val id: ItemId, val parent: Items = Items())
}

fun Application.configureItemRouting() {
    routing {
        get<Items.Details> { details ->
            logger.info { "Get details of item ${details.id.value}" }

            val state = STORE.getState()
            val item = state.items.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemDetails(call, state, item)
            }
        }
        get<Items.New> { new ->
            logger.info { "Add new item" }

            STORE.dispatch(CreateItem(new.template))

            call.respondRedirect(call.application.href(Items.Details(STORE.getState().items.lastId)))

            STORE.getState().save()
        }
        get<Items.Delete> { delete ->
            logger.info { "Delete item ${delete.id.value}" }

            STORE.dispatch(DeleteItem(delete.id))

            call.respondRedirect("/")

            STORE.getState().save()
        }
        get<Items.Edit> { edit ->
            logger.info { "Get editor for item ${edit.id.value}" }

            val state = STORE.getState()
            val item = state.items.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemEditor(call, state, item)
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

private fun HTML.showItemDetails(
    call: ApplicationCall,
    state: State,
    item: Item,
) {
    val name = state.getName(item.id)
    val deleteLink = call.application.href(Items.Delete(item.id))
    val editLink = call.application.href(Items.Edit(item.id))

    simpleHtml("Item: $name") {
        field("Id", item.id.value.toString())
        field("Template") {
            link(call, state, item.template)
        }
        when (item.location) {
            is InInventory -> {
                field("In Inventory of") {
                    link(call, state, item.location.character)
                }
            }

            UndefinedItemLocation -> doNothing()
        }

        p { a(editLink) { +"Edit" } }
        if (state.canDelete(item.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
    }
}

private fun HTML.showItemEditor(
    call: ApplicationCall,
    state: State,
    item: Item,
) {
    val name = state.getName(item.id)
    val backLink = href(call, item.id)
    val updateLink = call.application.href(Items.Update(item.id))

    simpleHtml("Edit Item: $name") {
        field("Id", item.id.value.toString())
        form {
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
