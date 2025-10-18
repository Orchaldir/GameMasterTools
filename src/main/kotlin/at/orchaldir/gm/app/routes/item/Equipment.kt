package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.editEquipment
import at.orchaldir.gm.app.html.item.equipment.parseEquipment
import at.orchaldir.gm.app.html.item.equipment.showEquipment
import at.orchaldir.gm.app.html.util.color.parseOptionalColorSchemeId
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.NormalMouth
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.SortEquipment
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.core.selector.culture.getFashions
import at.orchaldir.gm.core.selector.item.getEquippedBy
import at.orchaldir.gm.core.selector.util.getColors
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
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val height = fromMeters(1.0f)

@Resource("/$EQUIPMENT_TYPE")
class EquipmentRoutes : Routes<EquipmentId, SortEquipment> {
    @Resource("all")
    class All(
        val sort: SortEquipment = SortEquipment.Name,
        val parent: EquipmentRoutes = EquipmentRoutes(),
    )

    @Resource("gallery")
    class Gallery(
        val sort: SortEquipment = SortEquipment.Name,
        val parent: EquipmentRoutes = EquipmentRoutes(),
    )

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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortEquipment) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: EquipmentId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: EquipmentId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureEquipmentRouting() {
    routing {
        get<EquipmentRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                EquipmentRoutes(),
                state.sortEquipmentList(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Type") { tdEnum(it.data.getType()) },
                    Column("Weight") { td(it.weight) },
                    Column("Materials") { tdInlineIds(call, state, it.data.materials()) },
                    Column(listOf("Color", "Schemes")) { tdInlineIds(call, state, it.colorSchemes) },
                    Column(listOf("Required", "Colors")) { tdSkipZero(it.data.requiredSchemaColors()) },
                    Column("Characters") { tdSkipZero(state.getEquippedBy(it.id)) },
                    Column("Characters") { tdSkipZero(state.getFashions(it.id)) },
                ),
            )
        }
        get<EquipmentRoutes.Gallery> { gallery ->
            logger.info { "Show gallery" }

            call.respondHtml(HttpStatusCode.OK) {
                showGallery(call, STORE.getState(), gallery.sort)
            }
        }
        get<EquipmentRoutes.Details> { details ->
            handleShowElement(details.id, EquipmentRoutes(), HtmlBlockTag::showEquipmentDetails)
        }
        post<EquipmentRoutes.Scheme> { details ->
            val parameters = call.receiveParameters()
            val colorSchemeId = parseOptionalColorSchemeId(parameters, SCHEME)

            handleShowElement<EquipmentId, Equipment, SortEquipment>(
                details.id,
                EquipmentRoutes()
            ) { call, state, equipment ->
                showEquipmentDetails(call, state, equipment, colorSchemeId)
            }
        }
        get<EquipmentRoutes.New> {
            handleCreateElement(STORE.getState().getEquipmentStorage()) { id ->
                EquipmentRoutes.Edit(id)
            }
        }
        get<EquipmentRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, EquipmentRoutes())
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
            handleUpdateElement(update.id, ::parseEquipment)
        }
    }
}

private fun HTML.showGallery(
    call: ApplicationCall,
    state: State,
    sort: SortEquipment,
) {
    val equipmentList = state.sortEquipmentList(sort)
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

private fun HtmlBlockTag.showEquipmentDetails(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId? = null,
) {
    val characters = state.getEquippedBy(equipment.id)
    val fashions = state.getFashions(equipment.id)
    val previewLink = call.application.href(EquipmentRoutes.Scheme(equipment.id))

    if (equipment.colorSchemes.isNotEmpty()) {
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post

            selectColorSchemeToVisualizeEquipment(state, equipment, optionalColorSchemeId, 20)
        }
    } else {
        visualizeEquipment(state, equipment, UndefinedColors, 20)
    }

    showEquipment(call, state, equipment)

    h2 { +"Usage" }

    fieldElements(call, state, "Equipped By", characters)
    fieldElements(call, state, "Part of Fashion", fashions)
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

    simpleHtmlEditor(equipment, true) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editEquipment(state, equipment)
            }
        }, {
            if (equipment.colorSchemes.isNotEmpty()) {
                selectColorSchemeToVisualizeEquipment(state, equipment, optionalColorSchemeId, 60)
            } else {
                visualizeEquipment(state, equipment, UndefinedColors, 60)
            }
        })
    }
}

private fun HtmlBlockTag.selectColorSchemeToVisualizeEquipment(
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId?,
    width: Int,
) {
    val colorSchemeId = optionalColorSchemeId ?: equipment.colorSchemes.first()
    val colorScheme = state.getColorSchemeStorage().getOrThrow(colorSchemeId)
    val colorSchemes = state.getColorSchemeStorage().get(equipment.colorSchemes)

    selectElement(
        state,
        SCHEME,
        colorSchemes,
        colorSchemeId,
    )

    visualizeEquipment(state, equipment, colorScheme.data, width)
}

private fun HtmlBlockTag.visualizeEquipment(
    state: State,
    equipment: Equipment,
    colors: Colors,
    width: Int,
) {
    val equipped = EquipmentMap.from(equipment.data, colors)
    val appearance = createAppearance(equipment, height)
    val frontSvg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
    val backSvg = visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped, false)

    svg(frontSvg, width)
    svg(backSvg, width)
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