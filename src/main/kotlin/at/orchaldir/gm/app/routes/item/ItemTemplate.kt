package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.item.parseItemTemplate
import at.orchaldir.gm.core.action.CreateItemTemplate
import at.orchaldir.gm.core.action.DeleteItemTemplate
import at.orchaldir.gm.core.action.UpdateItemTemplate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.*
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.selector.getFashions
import at.orchaldir.gm.core.selector.item.canDelete
import at.orchaldir.gm.core.selector.item.getEquippedBy
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
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
class ItemTemplateRoutes {
    @Resource("details")
    class Details(val id: ItemTemplateId, val parent: ItemTemplateRoutes = ItemTemplateRoutes())

    @Resource("new")
    class New(val parent: ItemTemplateRoutes = ItemTemplateRoutes())

    @Resource("delete")
    class Delete(val id: ItemTemplateId, val parent: ItemTemplateRoutes = ItemTemplateRoutes())

    @Resource("edit")
    class Edit(val id: ItemTemplateId, val parent: ItemTemplateRoutes = ItemTemplateRoutes())

    @Resource("preview")
    class Preview(val id: ItemTemplateId, val parent: ItemTemplateRoutes = ItemTemplateRoutes())

    @Resource("update")
    class Update(val id: ItemTemplateId, val parent: ItemTemplateRoutes = ItemTemplateRoutes())
}

fun Application.configureItemTemplateRouting() {
    routing {
        get<ItemTemplateRoutes> {
            logger.info { "Get all item templates" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllItemTemplates(call)
            }
        }
        get<ItemTemplateRoutes.Details> { details ->
            logger.info { "Get details of item template ${details.id.value}" }

            val state = STORE.getState()
            val itemTemplate = state.getItemTemplateStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateDetails(call, state, itemTemplate)
            }
        }
        get<ItemTemplateRoutes.New> {
            logger.info { "Add new item template" }

            STORE.dispatch(CreateItemTemplate)

            call.respondRedirect(
                call.application.href(
                    ItemTemplateRoutes.Edit(
                        STORE.getState().getItemTemplateStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<ItemTemplateRoutes.Delete> { delete ->
            logger.info { "Delete item template ${delete.id.value}" }

            STORE.dispatch(DeleteItemTemplate(delete.id))

            call.respondRedirect(call.application.href(ItemTemplateRoutes()))

            STORE.getState().save()
        }
        get<ItemTemplateRoutes.Edit> { edit ->
            logger.info { "Get editor for item template ${edit.id.value}" }

            val state = STORE.getState()
            val template = state.getItemTemplateStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateEditor(call, state, template)
            }
        }
        post<ItemTemplateRoutes.Preview> { preview ->
            logger.info { "Get preview for item template ${preview.id.value}" }

            val template = parseItemTemplate(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showItemTemplateEditor(call, STORE.getState(), template)
            }
        }
        post<ItemTemplateRoutes.Update> { update ->
            logger.info { "Update item template ${update.id.value}" }

            val template = parseItemTemplate(update.id, call.receiveParameters())

            STORE.dispatch(UpdateItemTemplate(template))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllItemTemplates(call: ApplicationCall) {
    val templates = STORE.getState().getItemTemplateStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(ItemTemplateRoutes.New())

    simpleHtml("Item Templates") {
        field("Count", templates.size)
        showList(templates) { item ->
            link(call, item)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showItemTemplateDetails(
    call: ApplicationCall,
    state: State,
    template: ItemTemplate,
) {
    val characters = state.getEquippedBy(template.id)
    val fashions = state.getFashions(template.id)
    val backLink = call.application.href(ItemTemplateRoutes())
    val deleteLink = call.application.href(ItemTemplateRoutes.Delete(template.id))
    val editLink = call.application.href(ItemTemplateRoutes.Edit(template.id))

    simpleHtml("Item Template: ${template.name}") {
        visualizeItem(template)
        when (template.equipment) {
            NoEquipment -> doubleArrayOf()
            is Coat -> {
                field("Equipment", "Coat")
                field("Length", template.equipment.length)
                field("Neckline Style", template.equipment.necklineStyle)
                field("Sleeve Style", template.equipment.sleeveStyle)
                showOpeningStyle(template.equipment.openingStyle)
                showFill(template.equipment.fill)
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Dress -> {
                field("Equipment", "Dress")
                field("Neckline Style", template.equipment.necklineStyle)
                field("Skirt Style", template.equipment.skirtStyle)
                field("Sleeve Style", template.equipment.sleeveStyle)
                showFill(template.equipment.fill)
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Footwear -> {
                field("Equipment", "Footwear")
                field("Style", template.equipment.style)
                field("Color", template.equipment.color)
                if (template.equipment.style.hasSole()) {
                    field("Sole Color", template.equipment.sole)
                }
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Gloves -> {
                field("Equipment", "Gloves")
                field("Style", template.equipment.style)
                showFill(template.equipment.fill)
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Hat -> {
                field("Equipment", "Hat")
                field("Style", template.equipment.style)
                field("Color", template.equipment.color)
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Pants -> {
                field("Equipment", "Pants")
                field("Style", template.equipment.style)
                showFill(template.equipment.fill)
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Shirt -> {
                field("Equipment", "Shirt")
                field("Neckline Style", template.equipment.necklineStyle)
                field("Sleeve Style", template.equipment.sleeveStyle)
                showFill(template.equipment.fill)
                fieldLink("Material", call, state, template.equipment.material)
            }

            is Skirt -> {
                field("Equipment", "Skirt")
                field("Style", template.equipment.style)
                showFill(template.equipment.fill)
                fieldLink("Material", call, state, template.equipment.material)
            }
        }
        showList("Equipped By", characters) { item ->
            link(call, state, item)
        }
        showList("Part of Fashion", fashions) { item ->
            link(call, item)
        }
        action(editLink, "Edit")
        if (state.canDelete(template.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun BODY.showOpeningStyle(openingStyle: OpeningStyle) {
    field("Opening Style", openingStyle.javaClass.simpleName)
    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> showButtons(openingStyle.buttons)
        is DoubleBreasted -> {
            showButtons(openingStyle.buttons)
            field("Space between Columns", openingStyle.spaceBetweenColumns)
        }

        is Zipper -> {
            field("Zipper Color", openingStyle.color)
        }
    }
}

private fun BODY.showButtons(buttonColumn: ButtonColumn) {
    field("Button Count", buttonColumn.count.toString())
    field("Button Color", buttonColumn.button.color)
    field("Button Size", buttonColumn.button.size)
}

private fun HTML.showItemTemplateEditor(
    call: ApplicationCall,
    state: State,
    template: ItemTemplate,
) {
    val backLink = href(call, template.id)
    val previewLink = call.application.href(ItemTemplateRoutes.Preview(template.id))
    val updateLink = call.application.href(ItemTemplateRoutes.Update(template.id))

    simpleHtml("Edit Item Template: ${template.name}") {
        visualizeItem(template)
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(template.name)
            selectValue("Equipment", EQUIPMENT_TYPE, EquipmentType.entries, template.equipment.getType(), true)

            when (val equipment = template.equipment) {
                NoEquipment -> doNothing()
                is Coat -> {
                    selectValue("Length", LENGTH, OuterwearLength.entries, equipment.length, true)
                    selectNecklineStyle(NECKLINES_WITH_SLEEVES, equipment.necklineStyle)
                    selectSleeveStyle(SleeveStyle.entries, equipment.sleeveStyle)
                    selectOpeningStyle(equipment.openingStyle)
                    selectFill(equipment.fill)
                    selectMaterial(state, equipment.material)
                }

                is Dress -> {
                    selectNecklineStyle(NecklineStyle.entries, equipment.necklineStyle)
                    selectValue("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, equipment.skirtStyle, true)
                    selectSleeveStyle(
                        equipment.necklineStyle.getSupportsSleevesStyles(),
                        equipment.sleeveStyle,
                    )
                    selectFill(equipment.fill)
                    selectMaterial(state, equipment.material)
                }

                is Footwear -> {
                    selectValue("Style", FOOTWEAR, FootwearStyle.entries, equipment.style, true)
                    selectColor(equipment.color)
                    if (equipment.style.hasSole()) {
                        selectColor(equipment.sole, "Sole Color", EQUIPMENT_COLOR_1)
                    }
                    selectMaterial(state, equipment.material)
                }

                is Gloves -> {
                    selectValue("Style", GLOVES, GloveStyle.entries, equipment.style, true)
                    selectFill(equipment.fill)
                    selectMaterial(state, equipment.material)
                }

                is Hat -> {
                    selectValue("Style", HAT, HatStyle.entries, equipment.style, true)
                    selectColor(equipment.color)
                    selectMaterial(state, equipment.material)
                }

                is Pants -> {
                    selectValue("Style", PANTS, PantsStyle.entries, equipment.style, true)
                    selectFill(equipment.fill)
                    selectMaterial(state, equipment.material)
                }

                is Shirt -> {
                    selectNecklineStyle(NECKLINES_WITH_SLEEVES, equipment.necklineStyle)
                    selectSleeveStyle(
                        SleeveStyle.entries,
                        equipment.sleeveStyle,
                    )
                    selectFill(equipment.fill)
                    selectMaterial(state, equipment.material)
                }

                is Skirt -> {
                    selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, equipment.style, true)
                    selectFill(equipment.fill)
                    selectMaterial(state, equipment.material)
                }
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}

private fun FORM.selectNecklineStyle(options: Collection<NecklineStyle>, current: NecklineStyle) {
    selectValue("Neckline Style", NECKLINE_STYLE, options, current, true)
}

private fun FORM.selectOpeningStyle(openingStyle: OpeningStyle) {
    selectValue("Opening Style", OPENING_STYLE, OpeningType.entries, openingStyle.getType(), true)

    when (openingStyle) {
        NoOpening -> doNothing()
        is SingleBreasted -> selectButtons(openingStyle.buttons)
        is DoubleBreasted -> {
            selectButtons(openingStyle.buttons)
            selectValue(
                "Space between Columns",
                SPACE_BETWEEN_COLUMNS,
                Size.entries,
                openingStyle.spaceBetweenColumns,
                true
            )
        }

        is Zipper -> selectColor(openingStyle.color, "Zipper Color", ZIPPER)
    }
}

private fun FORM.selectButtons(buttonColumn: ButtonColumn) {
    selectInt("Button Count", buttonColumn.count.toInt(), 1, 20, 1, BUTTON_COUNT, true)
    selectColor(buttonColumn.button.color, "Button Color", BUTTON_COLOR)
    selectValue("Button Size", BUTTON_SIZE, Size.entries, buttonColumn.button.size, true)
}

private fun FORM.selectSleeveStyle(options: Collection<SleeveStyle>, current: SleeveStyle) {
    selectValue("Sleeve Style", SLEEVE_STYLE, options, current, true)
}

private fun FORM.selectMaterial(
    state: State,
    materialId: MaterialId,
) {
    selectValue("Material", MATERIAL, state.getMaterialStorage().getAll()) { material ->
        label = material.name
        value = material.id.value.toString()
        selected = materialId == material.id
    }
}

private fun BODY.visualizeItem(template: ItemTemplate) {
    if (template.equipment.getType() != EquipmentType.None) {
        val equipped = listOf(template.equipment)
        val appearance = HumanoidBody(Body(), Head(), Distance.fromMeters(1.0f))
        val frontSvg = visualizeCharacter(CHARACTER_CONFIG, appearance, equipped)
        val backSvg = visualizeCharacter(CHARACTER_CONFIG, appearance, equipped, false)
        svg(frontSvg, 20)
        svg(backSvg, 20)
    }
}