package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseItemTemplate
import at.orchaldir.gm.core.action.CreateItemTemplate
import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getItems
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

@Resource("/item_templates")
class ItemTemplates {
    @Resource("details")
    class Details(val id: ItemTemplateId, val parent: ItemTemplates = ItemTemplates())

    @Resource("new")
    class New(val parent: ItemTemplates = ItemTemplates())

    @Resource("delete")
    class Delete(val id: ItemTemplateId, val parent: ItemTemplates = ItemTemplates())

    @Resource("edit")
    class Edit(val id: ItemTemplateId, val parent: ItemTemplates = ItemTemplates())

    @Resource("update")
    class Update(val id: ItemTemplateId, val parent: ItemTemplates = ItemTemplates())
}

fun Application.configureItemTemplateRouting() {
    routing {
        get<ItemTemplates> {
            logger.info { "Get all item templates" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllItemTemplates(call)
            }
        }
        get<ItemTemplates.Details> { details ->
            logger.info { "Get details of item template ${details.id.value}" }

            val state = STORE.getState()
            val itemTemplate = state.itemTemplates.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateDetails(call, state, itemTemplate)
            }
        }
        get<ItemTemplates.New> {
            logger.info { "Add new item template" }

            STORE.dispatch(CreateItemTemplate)

            call.respondRedirect(call.application.href(ItemTemplates.Edit(STORE.getState().itemTemplates.lastId)))

            STORE.getState().save()
        }
        get<ItemTemplates.Delete> { delete ->
            logger.info { "Delete item template ${delete.id.value}" }

            STORE.dispatch(DeleteItemTemplate(delete.id))

            call.respondRedirect(call.application.href(ItemTemplates()))

            STORE.getState().save()
        }
        get<ItemTemplates.Edit> { edit ->
            logger.info { "Get editor for item template ${edit.id.value}" }

            val state = STORE.getState()
            val language = state.itemTemplates.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateEditor(call, language)
            }
        }
        post<ItemTemplates.Update> { update ->
            logger.info { "Update item template ${update.id.value}" }

            val nameList = parseItemTemplate(update.id, call.receiveParameters())

            STORE.dispatch(UpdateItemTemplate(nameList))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllItemTemplates(call: ApplicationCall) {
    val templates = STORE.getState().itemTemplates.getAll().sortedBy { it.name }
    val count = templates.size
    val createLink = call.application.href(ItemTemplates.New())

    simpleHtml("Item Templates") {
        field("Count", count.toString())
        showList(templates) { item ->
            link(call, item)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showItemTemplateDetails(
    call: ApplicationCall,
    state: State,
    itemTemplate: ItemTemplate,
) {
    val items = state.getItems(itemTemplate.id)
    val backLink = call.application.href(ItemTemplates())
    val deleteLink = call.application.href(ItemTemplates.Delete(itemTemplate.id))
    val editLink = call.application.href(ItemTemplates.Edit(itemTemplate.id))
    val createItemLink = call.application.href(Items.New(itemTemplate.id))

    simpleHtml("Item Template: ${itemTemplate.name}") {
        field("Id", itemTemplate.id.value.toString())
        showList("Instances", items) { item ->
            link(call, state, item)
            when (item.location) {
                is EquippedItem -> {
                    +" equipped by "
                    link(call, state, item.location.character)
                }

                is InInventory -> {
                    +" in "
                    link(call, state, item.location.character)
                    +"'s Inventory"
                }

                UndefinedItemLocation -> doNothing()
            }
        }
        p { a(editLink) { +"Edit" } }
        if (state.canDelete(itemTemplate.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(createItemLink) { +"Create Instance" } }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showItemTemplateEditor(
    call: ApplicationCall,
    nameList: ItemTemplate,
) {
    val backLink = href(call, nameList.id)
    val updateLink = call.application.href(ItemTemplates.Update(nameList.id))

    simpleHtml("Edit Item Template: ${nameList.name}") {
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
