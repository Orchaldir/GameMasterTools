package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.item.parseEquipment
import at.orchaldir.gm.core.action.CreateEquipment
import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.item.equipment.style.*
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

@Resource("/$EQUIPMENT_TYPE")
class EquipmentRoutes {
    @Resource("details")
    class Details(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("new")
    class New(val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("delete")
    class Delete(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("edit")
    class Edit(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("preview")
    class Preview(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("update")
    class Update(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())
}

fun Application.configureEquipmentRouting() {
    routing {
        get<EquipmentRoutes> {
            logger.info { "Get all equipments" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllEquipment(call)
            }
        }
        get<EquipmentRoutes.Details> { details ->
            logger.info { "Get details of equipment ${details.id.value}" }

            val state = STORE.getState()
            val equipment = state.getEquipmentStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentDetails(call, state, equipment)
            }
        }
        get<EquipmentRoutes.New> {
            logger.info { "Add new equipment" }

            STORE.dispatch(CreateEquipment)

            call.respondRedirect(
                call.application.href(
                    EquipmentRoutes.Edit(
                        STORE.getState().getEquipmentStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<EquipmentRoutes.Delete> { delete ->
            logger.info { "Delete equipment ${delete.id.value}" }

            STORE.dispatch(DeleteEquipment(delete.id))

            call.respondRedirect(call.application.href(EquipmentRoutes()))

            STORE.getState().save()
        }
        get<EquipmentRoutes.Edit> { edit ->
            logger.info { "Get editor for equipment ${edit.id.value}" }

            val state = STORE.getState()
            val template = state.getEquipmentStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, template)
            }
        }
        post<EquipmentRoutes.Preview> { preview ->
            logger.info { "Get preview for equipment ${preview.id.value}" }

            val equipment = parseEquipment(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, STORE.getState(), equipment)
            }
        }
        post<EquipmentRoutes.Update> { update ->
            logger.info { "Update equipment ${update.id.value}" }

            val equipment = parseEquipment(update.id, call.receiveParameters())

            STORE.dispatch(UpdateEquipment(equipment))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllEquipment(call: ApplicationCall) {
    val templates = STORE.getState().getEquipmentStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(EquipmentRoutes.New())

    simpleHtml("equipments") {
        field("Count", templates.size)
        showList(templates) { item ->
            link(call, item)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showEquipmentDetails(
    call: ApplicationCall,
    state: State,
    template: Equipment,
) {
    val characters = state.getEquippedBy(template.id)
    val fashions = state.getFashions(template.id)
    val backLink = call.application.href(EquipmentRoutes())
    val deleteLink = call.application.href(EquipmentRoutes.Delete(template.id))
    val editLink = call.application.href(EquipmentRoutes.Edit(template.id))

    simpleHtml("equipment: ${template.name}") {
        visualizeItem(template)
        when (template.data) {
            NoEquipment -> doubleArrayOf()
            is Coat -> {
                field("Equipment", "Coat")
                field("Length", template.data.length)
                field("Neckline Style", template.data.necklineStyle)
                field("Sleeve Style", template.data.sleeveStyle)
                showOpeningStyle(template.data.openingStyle)
                showFill(template.data.fill)
                fieldLink("Material", call, state, template.data.material)
            }

            is Dress -> {
                field("Equipment", "Dress")
                field("Neckline Style", template.data.necklineStyle)
                field("Skirt Style", template.data.skirtStyle)
                field("Sleeve Style", template.data.sleeveStyle)
                showFill(template.data.fill)
                fieldLink("Material", call, state, template.data.material)
            }

            is Footwear -> {
                field("Equipment", "Footwear")
                field("Style", template.data.style)
                field("Color", template.data.color)
                if (template.data.style.hasSole()) {
                    field("Sole Color", template.data.sole)
                }
                fieldLink("Material", call, state, template.data.material)
            }

            is Gloves -> {
                field("Equipment", "Gloves")
                field("Style", template.data.style)
                showFill(template.data.fill)
                fieldLink("Material", call, state, template.data.material)
            }

            is Hat -> {
                field("Equipment", "Hat")
                field("Style", template.data.style)
                field("Color", template.data.color)
                fieldLink("Material", call, state, template.data.material)
            }

            is Pants -> {
                field("Equipment", "Pants")
                field("Style", template.data.style)
                showFill(template.data.fill)
                fieldLink("Material", call, state, template.data.material)
            }

            is Shirt -> {
                field("Equipment", "Shirt")
                field("Neckline Style", template.data.necklineStyle)
                field("Sleeve Style", template.data.sleeveStyle)
                showFill(template.data.fill)
                fieldLink("Material", call, state, template.data.material)
            }

            is Skirt -> {
                field("Equipment", "Skirt")
                field("Style", template.data.style)
                showFill(template.data.fill)
                fieldLink("Material", call, state, template.data.material)
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

private fun HTML.showEquipmentEditor(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    val backLink = href(call, equipment.id)
    val previewLink = call.application.href(EquipmentRoutes.Preview(equipment.id))
    val updateLink = call.application.href(EquipmentRoutes.Update(equipment.id))

    simpleHtml("Edit equipment: ${equipment.name}") {
        visualizeItem(equipment)
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(equipment.name)
            selectValue(
                "Equipment",
                combine(EQUIPMENT, TYPE),
                EquipmentDataType.entries,
                equipment.data.getType(),
                true
            )

            when (val data = equipment.data) {
                NoEquipment -> doNothing()
                is Coat -> {
                    selectValue("Length", LENGTH, OuterwearLength.entries, data.length, true)
                    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
                    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
                    selectOpeningStyle(data.openingStyle)
                    selectFill(data.fill)
                    selectMaterial(state, data.material)
                }

                is Dress -> {
                    selectNecklineStyle(NecklineStyle.entries, data.necklineStyle)
                    selectValue("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, data.skirtStyle, true)
                    selectSleeveStyle(
                        data.necklineStyle.getSupportsSleevesStyles(),
                        data.sleeveStyle,
                    )
                    selectFill(data.fill)
                    selectMaterial(state, data.material)
                }

                is Footwear -> {
                    selectValue("Style", FOOTWEAR, FootwearStyle.entries, data.style, true)
                    selectColor(data.color)
                    if (data.style.hasSole()) {
                        selectColor(data.sole, "Sole Color", EQUIPMENT_COLOR_1)
                    }
                    selectMaterial(state, data.material)
                }

                is Gloves -> {
                    selectValue("Style", GLOVES, GloveStyle.entries, data.style, true)
                    selectFill(data.fill)
                    selectMaterial(state, data.material)
                }

                is Hat -> {
                    selectValue("Style", HAT, HatStyle.entries, data.style, true)
                    selectColor(data.color)
                    selectMaterial(state, data.material)
                }

                is Pants -> {
                    selectValue("Style", PANTS, PantsStyle.entries, data.style, true)
                    selectFill(data.fill)
                    selectMaterial(state, data.material)
                }

                is Shirt -> {
                    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
                    selectSleeveStyle(
                        SleeveStyle.entries,
                        data.sleeveStyle,
                    )
                    selectFill(data.fill)
                    selectMaterial(state, data.material)
                }

                is Skirt -> {
                    selectValue("Style", SKIRT_STYLE, SkirtStyle.entries, data.style, true)
                    selectFill(data.fill)
                    selectMaterial(state, data.material)
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
    selectElement(state, "Material", MATERIAL, state.getMaterialStorage().getAll(), materialId)
}

private fun BODY.visualizeItem(template: Equipment) {
    if (template.data.getType() != EquipmentDataType.None) {
        val equipped = listOf(template.data)
        val appearance = HumanoidBody(Body(), Head(), Distance.fromMeters(1.0f))
        val frontSvg = visualizeCharacter(CHARACTER_CONFIG, appearance, equipped)
        val backSvg = visualizeCharacter(CHARACTER_CONFIG, appearance, equipped, false)
        svg(frontSvg, 20)
        svg(backSvg, 20)
    }
}