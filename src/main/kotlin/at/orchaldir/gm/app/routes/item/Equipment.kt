package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.editEquipment
import at.orchaldir.gm.app.html.item.equipment.parseEquipment
import at.orchaldir.gm.app.html.item.equipment.showEquipment
import at.orchaldir.gm.app.html.util.color.parseOptionalColorSchemeId
import at.orchaldir.gm.core.action.CreateEquipment
import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.core.selector.culture.getFashions
import at.orchaldir.gm.core.selector.item.canDelete
import at.orchaldir.gm.core.selector.item.getEquippedBy
import at.orchaldir.gm.core.selector.util.getColors
import at.orchaldir.gm.core.selector.util.sortColorSchemes
import at.orchaldir.gm.core.selector.util.sortEquipmentList
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
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
private val height = fromMeters(1.0f)

@Resource("/$EQUIPMENT_TYPE")
class EquipmentRoutes {
    @Resource("gallery")
    class Gallery(val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("details")
    class Details(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

    @Resource("scheme")
    class Scheme(val id: EquipmentId, val parent: EquipmentRoutes = EquipmentRoutes())

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
                showAllEquipment(call, STORE.getState())
            }
        }
        get<EquipmentRoutes.Gallery> {
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState())
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
        post<EquipmentRoutes.Scheme> { details ->
            logger.info { "Get details of equipment ${details.id.value} with color schema" }

            val state = STORE.getState()
            val equipment = state.getEquipmentStorage().getOrThrow(details.id)
            val parameters = call.receiveParameters()
            val colorSchemeId = parseOptionalColorSchemeId(parameters, SCHEME)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentDetails(call, state, equipment, colorSchemeId)
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

            val state = STORE.getState()
            val parameters = call.receiveParameters()
            val equipment = parseEquipment(state, parameters, preview.id)
            val colorSchemeId = parseOptionalColorSchemeId(parameters, SCHEME)

            call.respondHtml(HttpStatusCode.OK) {
                showEquipmentEditor(call, state, equipment, colorSchemeId)
            }
        }
        post<EquipmentRoutes.Update> { update ->
            logger.info { "Update equipment ${update.id.value}" }

            val state = STORE.getState()
            val equipment = parseEquipment(state, call.receiveParameters(), update.id)

            STORE.dispatch(UpdateEquipment(equipment))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllEquipment(
    call: ApplicationCall,
    state: State,
) {
    val equipmentList = state.sortEquipmentList()
    val galleryLink = call.application.href(EquipmentRoutes.Gallery())
    val createLink = call.application.href(EquipmentRoutes.New())

    simpleHtml("Equipment") {
        action(galleryLink, "Gallery")
        field("Count", equipmentList.size)

        table {
            tr {
                th { +"Name" }
                th { +"Type" }
                th { +"Weight" }
                th { +"Materials" }
                thMultiLines(listOf("Color", "Schemes"))
                th { +"Characters" }
                th { +"Fashion" }
            }
            equipmentList.forEach { equipment ->
                tr {
                    tdLink(call, state, equipment)
                    tdEnum(equipment.data.getType())
                    td(equipment.weight)
                    tdInlineIds(call, state, equipment.data.materials())
                    tdSkipZero(equipment.colorSchemes.size)
                    tdSkipZero(state.getEquippedBy(equipment.id).size)
                    tdSkipZero(state.getFashions(equipment.id).size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
) {
    val equipmentList = state.sortEquipmentList()
    val backLink = call.application.href(EquipmentRoutes())

    simpleHtml("Equipment") {
        showGallery(call, state, equipmentList) { equipment ->
            val equipped = EquipmentMap.from(equipment.data, state.getColors(equipment))
            val appearance = createAppearance(equipment, height)

            visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
        }

        back(backLink)
    }
}

private fun HTML.showEquipmentDetails(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId? = null,
) {
    val characters = state.getEquippedBy(equipment.id)
    val fashions = state.getFashions(equipment.id)
    val backLink = call.application.href(EquipmentRoutes())
    val deleteLink = call.application.href(EquipmentRoutes.Delete(equipment.id))
    val editLink = call.application.href(EquipmentRoutes.Edit(equipment.id))
    val previewLink = call.application.href(EquipmentRoutes.Scheme(equipment.id))

    simpleHtmlDetails(equipment) {
        if (equipment.colorSchemes.isNotEmpty()) {
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post

                selectColorSchemeToVisualizeEquipment(state, equipment, optionalColorSchemeId)
            }
        } else {
            visualizeEquipment(state, equipment, UndefinedColors)
        }

        showEquipment(call, state, equipment)

        h2 { +"Usage" }

        fieldList(call, state, "Equipped By", characters)
        fieldList(call, state, "Part of Fashion", fashions)

        action(editLink, "Edit")

        if (state.canDelete(equipment.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showEquipmentEditor(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId? = null,
) {
    val backLink = href(call, equipment.id)
    val previewLink = call.application.href(EquipmentRoutes.Preview(equipment.id))
    val updateLink = call.application.href(EquipmentRoutes.Update(equipment.id))

    simpleHtmlEditor(equipment) {
        formWithPreview(previewLink, updateLink, backLink, canUpdate = equipment.areColorSchemesValid()) {
            if (equipment.colorSchemes.isNotEmpty()) {
                selectColorSchemeToVisualizeEquipment(state, equipment, optionalColorSchemeId)
            } else {
                visualizeEquipment(state, equipment, UndefinedColors)
            }

            editEquipment(state, equipment)
        }
    }
}

private fun HtmlBlockTag.selectColorSchemeToVisualizeEquipment(
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId?,
) {
    val colorSchemeId = optionalColorSchemeId ?: equipment.colorSchemes.first()
    val colorScheme = state.getColorSchemeStorage().getOrThrow(colorSchemeId)
    val colorSchemes = state.sortColorSchemes(state.getColorSchemeStorage().get(equipment.colorSchemes))

    selectElement(
        state,
        "Color Scheme",
        SCHEME,
        colorSchemes,
        colorSchemeId,
    )

    visualizeEquipment(state, equipment, colorScheme.data)
}

private fun HtmlBlockTag.visualizeEquipment(
    state: State,
    equipment: Equipment,
    colors: Colors,
) {
    val equipped = EquipmentMap.from(equipment.data, colors)
    val appearance = createAppearance(equipment, height)
    val frontSvg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
    val backSvg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped, false)

    svg(frontSvg, 20)
    svg(backSvg, 20)
}

private fun createAppearance(equipment: Equipment, height: Distance): Appearance {
    val head = Head(NormalEars(), TwoEyes(), mouth = NormalMouth())
    val appearance = if (requiresBody(equipment)) {
        HumanoidBody(Body(), head, height)
    } else {
        HeadOnly(head, height)
    }
    return appearance
}

private fun requiresBody(template: Equipment) = when (template.data.getType()) {
    EquipmentDataType.Earring -> false
    EquipmentDataType.EyePatch -> false
    EquipmentDataType.Glasses -> false
    EquipmentDataType.Hat -> false
    else -> true
}