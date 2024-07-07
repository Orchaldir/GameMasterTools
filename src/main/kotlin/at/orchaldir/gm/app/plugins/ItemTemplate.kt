package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateItemTemplate
import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
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

    @Resource("preview")
    class Preview(val id: ItemTemplateId, val parent: ItemTemplates = ItemTemplates())

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
            val template = state.itemTemplates.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateEditor(call, template)
            }
        }
        post<ItemTemplates.Preview> { preview ->
            logger.info { "Get preview for item template ${preview.id.value}" }

            val template = parseItemTemplate(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateEditor(call, template)
            }
        }
        post<ItemTemplates.Update> { update ->
            logger.info { "Update item template ${update.id.value}" }

            val template = parseItemTemplate(update.id, call.receiveParameters())

            STORE.dispatch(UpdateItemTemplate(template))

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
        when (itemTemplate.equipment) {
            NoEquipment -> doubleArrayOf()
            is Pants -> {
                field("Equipment", "Pants")
                field("Style", itemTemplate.equipment.style.toString())
                field("Color", itemTemplate.equipment.color.toString())
            }

            is Shirt -> {
                field("Equipment", "Shirt")
                field("Neckline Style", itemTemplate.equipment.necklineStyle.toString())
                field("Sleeve Style", itemTemplate.equipment.sleeveStyle.toString())
                field("Color", itemTemplate.equipment.color.toString())
            }

        }
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
    template: ItemTemplate,
) {
    val backLink = href(call, template.id)
    val previewLink = call.application.href(ItemTemplates.Preview(template.id))
    val updateLink = call.application.href(ItemTemplates.Update(template.id))

    simpleHtml("Edit Item Template: ${template.name}") {
        field("Id", template.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                textInput(name = "name") {
                    value = template.name
                }
            }
            selectEnum("Equipment", EQUIPMENT_TYPE, EquipmentType.entries, true) { type ->
                label = type.name
                value = type.name
                selected = when (template.equipment) {
                    NoEquipment -> type == EquipmentType.None
                    is Pants -> type == EquipmentType.Pants
                    is Shirt -> type == EquipmentType.Shirt
                }
            }
            when (template.equipment) {
                NoEquipment -> doNothing()
                is Pants -> {
                    selectEnum("Style", EQUIPMENT_STYLE, PantsStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor("Color", EQUIPMENT_COLOR, OneOf(Color.entries), template.equipment.color)
                }

                is Shirt -> {
                    selectEnum("Neckline Style", NECKLINE_STYLE, NecklineStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.necklineStyle == style
                    }
                    selectEnum("Sleeve Style", SLEEVE_STYLE, SleeveStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.sleeveStyle == style
                    }
                    selectColor("Color", EQUIPMENT_COLOR, OneOf(Color.entries), template.equipment.color)
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
