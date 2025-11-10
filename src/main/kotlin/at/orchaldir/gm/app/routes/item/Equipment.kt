package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.SCHEME
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.item.equipment.editEquipment
import at.orchaldir.gm.app.html.item.equipment.parseEquipment
import at.orchaldir.gm.app.html.item.equipment.showEquipment
import at.orchaldir.gm.app.html.rpg.combat.displayAttackEffect
import at.orchaldir.gm.app.html.rpg.combat.displayParrying
import at.orchaldir.gm.app.html.rpg.combat.displayProtection
import at.orchaldir.gm.app.html.rpg.combat.displayReach
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
import at.orchaldir.gm.core.selector.rpg.getArmorType
import at.orchaldir.gm.core.selector.util.getColors
import at.orchaldir.gm.core.selector.util.sortEquipmentList
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.visualization.character.appearance.visualizeCharacter
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*

private val height = fromMeters(1.0f)

@Resource("/$EQUIPMENT_TYPE")
class EquipmentRoutes : Routes<EquipmentId, SortEquipment> {
    @Resource("all")
    class All(
        val sort: SortEquipment = SortEquipment.Name,
        val parent: EquipmentRoutes = EquipmentRoutes(),
    )

    @Resource("armor")
    class AllArmors(
        val sort: SortEquipment = SortEquipment.Name,
        val parent: EquipmentRoutes = EquipmentRoutes(),
    )

    @Resource("melee")
    class AllMeleeWeapons(
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
    fun allArmors(call: ApplicationCall, sort: SortEquipment) = call.application.href(AllArmors(sort))
    fun allMeleeWeapons(call: ApplicationCall, sort: SortEquipment) = call.application.href(AllMeleeWeapons(sort))
    override fun delete(call: ApplicationCall, id: EquipmentId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: EquipmentId) = call.application.href(Edit(id))
    override fun gallery(call: ApplicationCall) = call.application.href(Gallery())
    override fun gallery(call: ApplicationCall, sort: SortEquipment) = call.application.href(Gallery(sort))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: EquipmentId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: EquipmentId) = call.application.href(Update(id))
}

fun Application.configureEquipmentRouting() {
    routing {
        get<EquipmentRoutes.All> { all ->
            val state = STORE.getState()
            val routes = EquipmentRoutes()

            handleShowAllElements(
                routes,
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
            ) {
                action(routes.allArmors(call, all.sort), "All Amors")
                action(routes.allMeleeWeapons(call, all.sort), "All Melee Weapons")
            }
        }
        get<EquipmentRoutes.AllArmors> { all ->
            val routes = EquipmentRoutes()
            val state = STORE.getState()
            val armors = state.getEquipmentStorage()
                .getAll()
                .filter { it.data.getArmorStats() != null }

            handleShowAllElements(
                routes,
                state.sortEquipmentList(armors, all.sort),
                listOf(
                    createNameColumn(call, state),
                    createIdColumn(call, state, "Type") { it.data.getArmorStats()?.type },
                    createIdColumn(call, state, "Material") { it.data.mainMaterial() },
                    tdColumn("Protection") {
                        state.getArmorType(it)
                            ?.let { type ->
                                displayProtection(call, state, type.protection)
                            }
                    },
                    Column("Modifiers") { tdInlineIds(call, state, it.data.getArmorStats()?.modifiers ?: emptySet()) },
                ),
            ) {
                action(routes.all(call, all.sort), "All")
            }
        }
        get<EquipmentRoutes.AllMeleeWeapons> { all ->
            val routes = EquipmentRoutes()
            val state = STORE.getState()
            val meleeWeapons = state.getEquipmentStorage()
                .getAll()
                .filter { it.data.getMeleeWeaponStats() != null }

            handleShowAllElements(
                routes,
                state.sortEquipmentList(meleeWeapons, all.sort),
                listOf(
                    createNameColumn(call, state),
                    createIdColumn(call, state, "Type") { it.data.getMeleeWeaponStats()?.type },
                    createIdColumn(call, state, "Material") { it.data.mainMaterial() },
                    Column("Modifiers") {
                        tdInlineIds(
                            call,
                            state,
                            it.data.getMeleeWeaponStats()?.modifiers ?: emptySet()
                        )
                    },
                    createMeleeWeaponColumn(state, "Damage") {
                        displayAttackEffect(call, state, it.effect)
                    },
                    createMeleeWeaponColumn(state, "Reach") {
                        displayReach(it.reach)
                    },
                    createMeleeWeaponColumn(state, "Parrying") {
                        displayParrying(it.parrying)
                    },
                ),
            ) {
                action(routes.all(call, all.sort), "All")
            }
        }
        get<EquipmentRoutes.Gallery> { gallery ->
            val state = STORE.getState()
            val routes = EquipmentRoutes()

            handleShowGallery(
                state,
                routes,
                state.sortEquipmentList(gallery.sort),
                gallery.sort,
            ) { equipment ->
                val equipped = EquipmentMap.from(equipment.data, state.getColors(equipment))
                val appearance = createAppearance(equipment, height)

                visualizeCharacter(state, CHARACTER_CONFIG, appearance, equipped)
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
            handleCreateElement(EquipmentRoutes(), STORE.getState().getEquipmentStorage())
        }
        get<EquipmentRoutes.Delete> { delete ->
            handleDeleteElement(EquipmentRoutes(), delete.id)
        }
        get<EquipmentRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                EquipmentRoutes(),
                HtmlBlockTag::editEquipment,
                HtmlBlockTag::showEquipmentEditorRight,
            )
        }
        post<EquipmentRoutes.Preview> { preview ->
            val parameters = call.receiveParameters()
            val colorSchemeId = parseOptionalColorSchemeId(parameters, SCHEME)

            handlePreviewElementSplit(
                preview.id,
                parameters,
                EquipmentRoutes(),
                ::parseEquipment,
                HtmlBlockTag::editEquipment,
            ) { call, state, equipment ->
                showEquipmentEditorRight(call, state, equipment, colorSchemeId)
            }
        }
        post<EquipmentRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseEquipment)
        }
    }
}

private fun HtmlBlockTag.showEquipmentDetails(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId? = null,
) {
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
    showUsage(call, state, equipment)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
) {
    val characters = state.getEquippedBy(equipment.id)
    val fashions = state.getFashions(equipment.id)

    if (characters.isEmpty() && fashions.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, "Equipped By", characters)
    fieldElements(call, state, "Part of Fashion", fashions)
}

private fun HtmlBlockTag.showEquipmentEditorRight(
    call: ApplicationCall,
    state: State,
    equipment: Equipment,
    optionalColorSchemeId: ColorSchemeId? = null,
) {
    if (equipment.colorSchemes.isNotEmpty()) {
        selectColorSchemeToVisualizeEquipment(state, equipment, optionalColorSchemeId, 60)
    } else {
        visualizeEquipment(state, equipment, UndefinedColors, 60)
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