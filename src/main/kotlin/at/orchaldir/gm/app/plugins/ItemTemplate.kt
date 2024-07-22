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
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.core.model.material.MaterialId
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
                showItemTemplateEditor(call, state, template)
            }
        }
        post<ItemTemplates.Preview> { preview ->
            logger.info { "Get preview for item template ${preview.id.value}" }

            val template = parseItemTemplate(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateEditor(call, STORE.getState(), template)
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
    template: ItemTemplate,
) {
    val items = state.getItems(template.id)
    val backLink = call.application.href(ItemTemplates())
    val deleteLink = call.application.href(ItemTemplates.Delete(template.id))
    val editLink = call.application.href(ItemTemplates.Edit(template.id))
    val createItemLink = call.application.href(Items.New(template.id))

    simpleHtml("Item Template: ${template.name}") {
        field("Id", template.id.value.toString())
        when (template.equipment) {
            NoEquipment -> doubleArrayOf()
            is Dress -> {
                field("Equipment", "Dress")
                field("Neckline Style", template.equipment.necklineStyle.toString())
                field("Skirt Style", template.equipment.skirtStyle.toString())
                field("Sleeve Style", template.equipment.sleeveStyle.toString())
                field("Color", template.equipment.color.toString())
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

            is Footwear -> {
                field("Equipment", "Footwear")
                field("Style", template.equipment.style.toString())
                field("Color", template.equipment.color.toString())
                field("Sole Color", template.equipment.sole.toString())
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

            is Hat -> {
                field("Equipment", "Hat")
                field("Style", template.equipment.style.toString())
                field("Color", template.equipment.color.toString())
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

            is Pants -> {
                field("Equipment", "Pants")
                field("Style", template.equipment.style.toString())
                field("Color", template.equipment.color.toString())
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

            is Shirt -> {
                field("Equipment", "Shirt")
                field("Neckline Style", template.equipment.necklineStyle.toString())
                field("Sleeve Style", template.equipment.sleeveStyle.toString())
                field("Color", template.equipment.color.toString())
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

            is Skirt -> {
                field("Equipment", "Skirt")
                field("Style", template.equipment.style.toString())
                field("Color", template.equipment.color.toString())
                field("Material") {
                    link(call, state, template.equipment.material)
                }
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
        if (state.canDelete(template.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(createItemLink) { +"Create Instance" } }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showItemTemplateEditor(
    call: ApplicationCall,
    state: State,
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
                selected = template.equipment.isType(type)
            }
            when (template.equipment) {
                NoEquipment -> doNothing()
                is Dress -> {
                    selectEnum("Neckline Style", NECKLINE_STYLE, NecklineStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.necklineStyle == style
                    }
                    selectEnum("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.skirtStyle == style
                    }
                    val sleevesStyles = template.equipment.necklineStyle.getSupportsSleevesStyles()
                    selectEnum("Sleeve Style", SLEEVE_STYLE, sleevesStyles, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.sleeveStyle == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
                }

                is Footwear -> {
                    selectEnum("Style", EQUIPMENT_STYLE, FootwearStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    selectColor(template.equipment.sole, "Sole Color", SOLE_COLOR)
                    selectMaterial(state, template.equipment.material)
                }

                is Hat -> {
                    selectEnum("Style", EQUIPMENT_STYLE, HatStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
                }

                is Pants -> {
                    selectEnum("Style", EQUIPMENT_STYLE, PantsStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
                }

                is Shirt -> {
                    selectEnum("Neckline Style", NECKLINE_STYLE, NecklineStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.necklineStyle == style
                    }
                    val sleevesStyles = template.equipment.necklineStyle.getSupportsSleevesStyles()
                    selectEnum("Sleeve Style", SLEEVE_STYLE, sleevesStyles, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.sleeveStyle == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
                }

                is Skirt -> {
                    selectEnum("Style", SKIRT_STYLE, SkirtStyle.entries, false) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
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

private fun FORM.selectMaterial(
    state: State,
    materialId: MaterialId,
) {
    selectEnum("Material", MATERIAL, state.materials.getAll()) { material ->
        label = material.name
        value = material.id.value.toString()
        selected = materialId == material.id
    }
}

private fun FORM.selectColor(color: Color, label: String = "Color", selectId: String = EQUIPMENT_COLOR) {
    selectColor(label, selectId, OneOf(Color.entries), color)
}
