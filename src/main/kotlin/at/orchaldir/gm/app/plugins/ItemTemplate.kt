package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.action.CreateItemTemplate
import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.*
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getEquippedBy
import at.orchaldir.gm.core.selector.getFashions
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.visualization.character.visualizeCharacter
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
    val characters = state.getEquippedBy(template.id)
    val fashions = state.getFashions(template.id)
    val backLink = call.application.href(ItemTemplates())
    val deleteLink = call.application.href(ItemTemplates.Delete(template.id))
    val editLink = call.application.href(ItemTemplates.Edit(template.id))

    simpleHtml("Item Template: ${template.name}") {
        visualizeItem(template)
        field("Id", template.id.value.toString())
        when (template.equipment) {
            NoEquipment -> doubleArrayOf()
            is Dress -> {
                field("Equipment", "Dress")
                field("Neckline Style", template.equipment.necklineStyle.toString())
                field("Skirt Style", template.equipment.skirtStyle.toString())
                field("Sleeve Style", template.equipment.sleeveStyle.toString())
                showFill(template.equipment.fill)
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

            is Footwear -> {
                field("Equipment", "Footwear")
                field("Style", template.equipment.style.toString())
                field("Color", template.equipment.color.toString())
                if (template.equipment.style.hasSole()) {
                    field("Sole Color", template.equipment.sole.toString())
                }
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
                showFill(template.equipment.fill)
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
        showList("Equipped By", characters) { item ->
            link(call, state, item)
        }
        showList("Part of Fashion", fashions) { item ->
            link(call, item)
        }
        p { a(editLink) { +"Edit" } }
        if (state.canDelete(template.id)) {
            p { a(deleteLink) { +"Delete" } }
        }
        p { a(backLink) { +"Back" } }
    }
}

private fun BODY.showFill(fill: Fill) {
    when (fill) {
        is Solid -> field("Color", fill.color.toString())
        is VerticalStripes -> field("Vertical Stripes", "${fill.color0} & ${fill.color1}")
        is HorizontalStripes -> field("Horizontal Stripes", "${fill.color0} & ${fill.color1}")
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
        visualizeItem(template)
        field("Id", template.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            field("Name") {
                textInput(name = NAME) {
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
                    selectEnum("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.skirtStyle == style
                    }
                    val sleevesStyles = template.equipment.necklineStyle.getSupportsSleevesStyles()
                    selectEnum("Sleeve Style", SLEEVE_STYLE, sleevesStyles, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.sleeveStyle == style
                    }
                    selectFill(template.equipment.fill)
                    selectMaterial(state, template.equipment.material)
                }

                is Footwear -> {
                    selectEnum("Style", FOOTWEAR, FootwearStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    if (template.equipment.style.hasSole()) {
                        selectColor(template.equipment.sole, "Sole Color", EQUIPMENT_COLOR_1)
                    }
                    selectMaterial(state, template.equipment.material)
                }

                is Hat -> {
                    selectEnum("Style", HAT, HatStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
                }

                is Pants -> {
                    selectEnum("Style", PANTS, PantsStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectColor(template.equipment.color)
                    selectMaterial(state, template.equipment.material)
                }

                is Shirt -> {
                    selectEnum("Neckline Style", NECKLINE_STYLE, NecklineStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.necklineStyle == style
                    }
                    val sleevesStyles = template.equipment.necklineStyle.getSupportsSleevesStyles()
                    selectEnum("Sleeve Style", SLEEVE_STYLE, sleevesStyles, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.sleeveStyle == style
                    }
                    selectFill(template.equipment.fill)
                    selectMaterial(state, template.equipment.material)
                }

                is Skirt -> {
                    selectEnum("Style", SKIRT_STYLE, SkirtStyle.entries, true) { style ->
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

private fun FORM.selectFill(fill: Fill) {
    selectEnum("Fill Type", FILL_TYPE, FillType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = when (fill) {
            is Solid -> type == FillType.Solid
            is VerticalStripes -> type == FillType.VerticalStripes
            is HorizontalStripes -> type == FillType.HorizontalStripes
        }
    }
    when (fill) {
        is Solid -> selectColor(fill.color)
        is VerticalStripes -> selectStripes(fill.color0, fill.color1, fill.width)
        is HorizontalStripes -> selectStripes(fill.color0, fill.color1, fill.width)
    }
}

private fun FORM.selectStripes(color0: Color, color1: Color, width: UByte) {
    selectColor(color0, "1.Stripe Color")
    selectColor(color1, "2.Stripe Color", EQUIPMENT_COLOR_1)
    field("Stripe Width") {
        numberInput(name = PATTERN_WIDTH) {
            min = "1"
            max = "10"
            value = width.toString()
            onChange = ON_CHANGE_SCRIPT
        }
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

private fun FORM.selectColor(color: Color, label: String = "Color", selectId: String = EQUIPMENT_COLOR_0) {
    selectColor(label, selectId, OneOf(Color.entries), color)
}

private fun BODY.visualizeItem(template: ItemTemplate) {
    if (template.equipment.getType() != EquipmentType.None) {
        val equipped = listOf(template.equipment)
        val appearance = HumanoidBody(Body(), Head(), Distance(1.0f))
        val frontSvg = visualizeCharacter(RENDER_CONFIG, appearance, equipped)
        val backSvg = visualizeCharacter(RENDER_CONFIG, appearance, equipped, false)
        svg(frontSvg, 20)
        svg(backSvg, 20)
    }
}