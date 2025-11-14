package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeHeadFixation
import at.orchaldir.gm.visualization.utils.visualizeComplexShape

data class ClubConfig(
    val simpleHeight: SizeConfig<Factor>,
    val oneHandedHeight: Factor,
    val twoHandedHeight: Factor,
    val shaftThickness: Factor,
) {
    fun shaftAabb(
        state: CharacterRenderState,
        body: Body,
        isOneHanded: Boolean,
        hand: Point2d,
    ): AABB {
        val handRadius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
        val bottom = hand.addHeight(handRadius * 2)
        val heightFactor = if (isOneHanded) {
            oneHandedHeight
        } else {
            twoHandedHeight
        }
        val height = state.aabb.convertHeight(heightFactor)
        val width = state.aabb.convertHeight(shaftThickness)

        return AABB.fromBottom(bottom, Size2d(width, height))
    }
}

fun visualizeClub(
    state: CharacterRenderState,
    body: Body,
    head: ClubHead,
    size: Size,
    shaft: Shaft,
    fixation: HeadFixation,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER)
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val config = state.config.equipment.club
    val shaftAabb = config.shaftAabb(state, body, isOneHanded, hand)

    visualizeClubHead(state, renderer, config, shaftAabb, head, size)
    visualizePolearmShaft(state, renderer, shaftAabb, shaft, NoPolearmHead)
}

fun visualizeClubHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: ClubConfig,
    shaftAabb: AABB,
    head: ClubHead,
    size: Size,
) = when (head) {
    is SimpleClubHead -> visualizeSimpleClubHead(state, renderer, config, shaftAabb, head, size)
}

private fun visualizeSimpleClubHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleClubHead,
    size: Size,
) {
    val radiusFactor = config.simpleHeight.convert(size) / 2
    val radius = shaftAabb.convertHeight(radiusFactor)
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)

    val color = head.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    visualizeComplexShape(renderer, center, radius, head.shape, options)
}
