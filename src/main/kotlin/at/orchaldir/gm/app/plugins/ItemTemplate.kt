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
            is Coat -> {
                field("Equipment", "Coat")
                field("Length", template.equipment.length.toString())
                field("Neckline Style", template.equipment.necklineStyle.toString())
                field("Sleeve Style", template.equipment.sleeveStyle.toString())
                showOpeningStyle(template.equipment.openingStyle)
                showFill(template.equipment.fill)
                field("Material") {
                    link(call, state, template.equipment.material)
                }
            }

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

            is Gloves -> {
                field("Equipment", "Gloves")
                field("Style", template.equipment.style.toString())
                showFill(template.equipment.fill)
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
                showFill(template.equipment.fill)
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
                showFill(template.equipment.fill)
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

private fun BODY.showOpeningStyle(openingStyle: OpeningStyle) {
    field("Opening Style", openingStyle.javaClass.simpleName)
    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> showButtons(openingStyle.buttons)
        is DoubleBreasted -> {
            showButtons(openingStyle.buttons)
            field("Space between Columns", openingStyle.spaceBetweenColumns.toString())
        }

        is Zipper -> {
            field("Zipper Color", openingStyle.color.toString())
        }
    }
}

private fun BODY.showButtons(buttonColumn: ButtonColumn) {
    field("Button Count", buttonColumn.count.toString())
    field("Button Color", buttonColumn.button.color.toString())
    field("Button Size", buttonColumn.button.size.toString())
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
                is Coat -> {
                    selectEnum("Length", LENGTH, OuterwearLength.entries, true) { length ->
                        label = length.name
                        value = length.name
                        selected = template.equipment.length == length
                    }
                    selectNecklineStyle(NECKLINES_WITH_SLEEVES, template.equipment.necklineStyle)
                    selectSleeveStyle(SleeveStyle.entries, template.equipment.sleeveStyle)
                    selectOpeningStyle(template.equipment.openingStyle)
                    selectFill(template.equipment.fill)
                    selectMaterial(state, template.equipment.material)
                }

                is Dress -> {
                    selectNecklineStyle(NecklineStyle.entries, template.equipment.necklineStyle)
                    selectEnum("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.skirtStyle == style
                    }
                    selectSleeveStyle(
                        template.equipment.necklineStyle.getSupportsSleevesStyles(),
                        template.equipment.sleeveStyle,
                    )
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

                is Gloves -> {
                    selectEnum("Style", GLOVES, GloveStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectFill(template.equipment.fill)
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
                    selectFill(template.equipment.fill)
                    selectMaterial(state, template.equipment.material)
                }

                is Shirt -> {
                    selectNecklineStyle(NECKLINES_WITH_SLEEVES, template.equipment.necklineStyle)
                    selectSleeveStyle(
                        SleeveStyle.entries,
                        template.equipment.sleeveStyle,
                    )
                    selectFill(template.equipment.fill)
                    selectMaterial(state, template.equipment.material)
                }

                is Skirt -> {
                    selectEnum("Style", SKIRT_STYLE, SkirtStyle.entries, true) { style ->
                        label = style.name
                        value = style.name
                        selected = template.equipment.style == style
                    }
                    selectFill(template.equipment.fill)
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

private fun FORM.selectNecklineStyle(options: Collection<NecklineStyle>, current: NecklineStyle) {
    selectEnum("Neckline Style", NECKLINE_STYLE, options, true) { style ->
        label = style.name
        value = style.name
        selected = current == style
    }
}

private fun FORM.selectOpeningStyle(openingStyle: OpeningStyle) {
    selectEnum("Opening Style", OPENING_STYLE, OpeningType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = when (openingStyle) {
            NoOpening -> type == OpeningType.NoOpening
            is SingleBreasted -> type == OpeningType.SingleBreasted
            is DoubleBreasted -> type == OpeningType.DoubleBreasted
            is Zipper -> type == OpeningType.Zipper
        }
    }
    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> selectButtons(openingStyle.buttons)
        is DoubleBreasted -> {
            selectButtons(openingStyle.buttons)
            selectEnum("Space between Columns", SPACE_BETWEEN_COLUMNS, Size.entries, true) { space ->
                label = space.name
                value = space.name
                selected = space == openingStyle.spaceBetweenColumns
            }
        }

        is Zipper -> selectColor(openingStyle.color, "Zipper Color", ZIPPER)
    }
}

private fun FORM.selectButtons(buttonColumn: ButtonColumn) {
    selectNumber("Button Count", buttonColumn.count.toInt(), 1, 20, BUTTON_COUNT, true)
    selectColor(buttonColumn.button.color, "Button Color", BUTTON_COLOR)
    selectEnum("Button Size", BUTTON_SIZE, Size.entries, true) { size ->
        label = size.name
        value = size.name
        selected = size == buttonColumn.button.size
    }
}

private fun FORM.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectEnum("Sleeve Style", SLEEVE_STYLE, options, true) { style ->
        label = style.name
        value = style.name
        selected = current == style
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
    selectColor(color0, "1.Stripe Color", colors = Color.entries - color1)
    selectColor(color1, "2.Stripe Color", EQUIPMENT_COLOR_1, Color.entries - color0)
    selectNumber("Stripe Width", width.toInt(), 1, 10, PATTERN_WIDTH, true)
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

private fun FORM.selectColor(
    color: Color,
    label: String = "Color",
    selectId: String = EQUIPMENT_COLOR_0,
    colors: Collection<Color> = Color.entries,
) {
    selectColor(label, selectId, OneOf(colors), color)
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