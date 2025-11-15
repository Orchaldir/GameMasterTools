package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.RenderOptions
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

    fun extendShaft(shaftAabb: AABB, head: ClubHead, headSize: Size) = when(head) {
        is SimpleFlangedHead -> shaftAabb.growBottom(simpleHeight.convert(headSize))
        else -> shaftAabb
    }

    fun getExtraFixationHeight(head: ClubHead, headSize: Size) = when (head) {
        is SimpleClubHead -> ZERO
        is SimpleFlangedHead -> {
            val extra = simpleHeight.convert(headSize)
            extra / (ONE + extra)
        }
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
    val extendedShaftAabb = config.extendShaft(shaftAabb, head, size)
    val extraHeight = config.getExtraFixationHeight(head, size)

    visualizePolearmShaft(state, renderer, extendedShaftAabb, shaft, NoPolearmHead)
    visualizeHeadFixation(state, extendedShaftAabb, fixation, extraHeight)
    visualizeClubHead(state, HELD_EQUIPMENT_LAYER, config, shaftAabb, head, size)
}

fun visualizeClubHead(
    state: CharacterRenderState,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: ClubHead,
    size: Size,
) = when (head) {
    is SimpleClubHead -> visualizeSimpleClubHead(state, layer, config, shaftAabb, head, size)
    is SimpleFlangedHead -> visualizeSimpleFlangedHead(state, layer, config, shaftAabb, head, size)
}

private fun visualizeSimpleClubHead(
    state: CharacterRenderState,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleClubHead,
    size: Size,
) {
    val renderer = state.getLayer(layer)
    val radiusFactor = config.simpleHeight.convert(size) / 2
    val radius = shaftAabb.convertHeight(radiusFactor)
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)

    val color = head.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    visualizeComplexShape(renderer, center, radius, head.shape, options)
}

private fun visualizeSimpleFlangedHead(
    state: CharacterRenderState,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleFlangedHead,
    size: Size,
) {
    val color = head.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    visualizeSimpleSideFlanges(state, options, layer, config, shaftAabb, head, size)
    visualizeSimpleMiddleFlange(state, options, layer, config, shaftAabb, size)
}

private fun visualizeSimpleSideFlanges(
    state: CharacterRenderState,
    options: RenderOptions,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleFlangedHead,
    size: Size,
) {
    val renderer = state.getLayer(layer, -1)
    val heightFactor = config.simpleHeight.convert(size)
    val radiusFactor = heightFactor / 2
    val radius = shaftAabb.convertHeight(radiusFactor)
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)

    visualizeComplexShape(renderer, center, radius, head.shape, options)
}

private fun visualizeSimpleMiddleFlange(
    state: CharacterRenderState,
    options: RenderOptions,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    size: Size,
) {
    val renderer = state.getLayer(layer, 2)
    val heightFactor = config.simpleHeight.convert(size)
    val radiusFactor = heightFactor / 2
    val aabb = shaftAabb.createSubAabb(CENTER, -radiusFactor, HALF, heightFactor)

    renderer.renderRectangle(aabb, options)
}
