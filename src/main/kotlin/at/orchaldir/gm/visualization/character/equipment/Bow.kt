package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import at.orchaldir.gm.utils.renderer.TransformRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER

data class BowConfig(
    val heightToWidth: Factor,
    val thicknessCenter: Factor,
)

fun visualizeBow(
    state: CharacterRenderState<Body>,
    bow: Bow,
    set: Set<BodySlot>,
) {
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)

    state.getLayer(HELD_EQUIPMENT_LAYER)
        .createGroup(hand) { movedRenderer ->
            movedRenderer.createGroup(QUARTER_CIRCLE) { rotatedRender ->
                visualizeBow(state, rotatedRender, bow)
            }
        }
}

private fun visualizeBow(
    state: CharacterRenderState<Body>,
    renderer: TransformRenderer,
    bow: Bow,
) {
    val height = state.fullAABB.convertHeight(bow.height)
    val width = height * state.config.equipment.bow.heightToWidth
    val thickness = height * state.config.equipment.bow.thicknessCenter
    val centerX = (thickness - width) / 2.0f
    val aabb = AABB.fromWidthAndHeight(Point2d.xAxis(centerX), width, height)

    visualizeBowShape(state, renderer, aabb, bow)
}

private fun visualizeBowShape(
    state: CharacterRenderState<Body>,
    renderer: TransformRenderer,
    aabb: AABB,
    bow: Bow,
) {
    val fill = bow.fill.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    renderer.renderRectangle(aabb, options)

    when (bow.shape) {
        BowShape.Angular -> doNothing()
        BowShape.Straight -> doNothing()
    }
}


