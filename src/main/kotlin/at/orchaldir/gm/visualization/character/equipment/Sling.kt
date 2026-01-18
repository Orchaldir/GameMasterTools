package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Sling
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.SwingConfig
import at.orchaldir.gm.visualization.character.equipment.part.visualizeLineStyle

data class SlingConfig(
    // Relative to max length
    val cordLength: Factor,
    // Relative to hand radius
    val cordThickness: Factor,
    // Relative to hand radius
    val cradleWidth: SizeConfig<Factor>,
    // Relative to width
    val cradleHeight: Factor,
    val swing: SwingConfig,
)

fun visualizeSling(
    state: CharacterRenderState<Body>,
    sling: Sling,
    set: Set<BodySlot>,
) {
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val maxLength = state.fullAABB.getEnd().y - hand.y

    state.getLayer(HELD_EQUIPMENT_LAYER)
        .createGroup(hand) { movedRenderer ->
        state.config.equipment.sling.swing.createSwingGroup(movedRenderer) { renderer ->
            visualizeSling(state, renderer, sling, maxLength)
        }
    }
}

private fun visualizeSling(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    sling: Sling,
    maxLength: Distance,
) {
    val config = state.config.equipment.sling
    val handRadius = state.config.body.getHandRadius(state)
    val cradleThickness = handRadius * config.cordThickness
    val cradleCenter = Point2d.yAxis(maxLength * config.cordLength)
    val cradleWidth = config.cradleWidth.convert(sling.size)
    val cradleSize = Size2d.square(handRadius * 2)
        .scale(cradleWidth, cradleWidth * config.cradleHeight)
    val cradleAabb = AABB.fromCenter(cradleCenter, cradleSize)
    val cradleFill = sling.cradle.getFill(state.state, state.colors)
    val cradleOptions = FillAndBorder(cradleFill.toRender(), state.config.line)

    renderer.renderRectangle(cradleAabb, cradleOptions)

    cradleAabb.getMirroredPoints(FULL, HALF)
        .toList()
        .forEach { end ->
            visualizeLineStyle(
                state,
                renderer,
                sling.cord,
                Line2d(Point2d(), end),
                cradleThickness,
            )
        }
}
