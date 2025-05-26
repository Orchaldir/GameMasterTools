package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Polearm
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.visualizeSegments

data class PolearmConfig(
    val length: Factor,
    val width: Factor,
) {
    fun getLength(aabb: AABB) = aabb.convertHeight(length)
    fun getWidth(aabb: AABB) = aabb.convertHeight(width)
}

fun visualizePolearm(
    state: CharacterRenderState,
    body: Body,
    polearm: Polearm,
    set: Set<BodySlot>,
) {
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val config = state.config.equipment.polearm
    val length = config.getLength(state.aabb)
    val width = config.getWidth(state.aabb)
    val y = state.aabb.getEnd().y - length / 2
    val left = Point2d(leftHand.x, y)
    val right = Point2d(rightHand.x, y)
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER)
    val center = state.getCenter(left, right, set, BodySlot.HeldInRightHand)
    val shaftAabb = AABB.fromWidthAndHeight(center, width, length)

    visualizePolearmShaft(state, renderer, shaftAabb, polearm)
    visualizePolearmHead(state, renderer, shaftAabb, polearm)
}

private fun visualizePolearmHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    polearm: Polearm,
) {
    when (polearm.head) {
        NoPolearmHead, RoundedPolearmHead, SharpenedPolearmHead -> doNothing()
        is PolearmHeadWithSegments -> visualizeSegments(
            state,
            polearm.head.segments,
            aabb.getPoint(CENTER, START),
            true,
            aabb.size.height,
            aabb.size.width,
        )
    }
}

private fun visualizePolearmShaft(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    polearm: Polearm,
) {
    when (polearm.shaft) {
        is SimpleShaft -> {
            val fill = polearm.shaft.part.getFill(state.state, state.colors)
            val options = FillAndBorder(fill.toRender(), state.config.line)
            val polygon = createSimpleShaftPolygon(aabb, polearm.head)

            renderer.renderRoundedPolygon(polygon, options)
        }
    }
}

private fun createSimpleShaftPolygon(
    aabb: AABB,
    head: PolearmHead,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (head) {
        NoPolearmHead, is PolearmHeadWithSegments -> builder
            .addMirroredPoints(aabb, FULL, START, true)
        RoundedPolearmHead -> builder
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, Factor.fromPercentage(10))
        SharpenedPolearmHead -> builder
            .addLeftPoint(aabb, CENTER, START, true)
            .addMirroredPoints(aabb, FULL, Factor.fromPercentage(10), true)
    }

    return builder
        .addMirroredPoints(aabb, FULL, END, true)
        .build()
}
