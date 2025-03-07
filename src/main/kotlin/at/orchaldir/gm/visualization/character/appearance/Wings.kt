package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeWings(state: CharacterRenderState, wings: Wings) = when (wings) {
    NoWings -> doNothing()
    is OneWing -> visualizeWing(state, wings.wing, wings.side)
    is TwoWings -> {
        visualizeWing(state, wings.wing, Side.Left)
        visualizeWing(state, wings.wing, Side.Right)
    }

    is DifferentWings -> {
        visualizeWing(state, wings.left, Side.Left)
        visualizeWing(state, wings.right, Side.Right)
    }
}

private fun visualizeWing(state: CharacterRenderState, wing: Wing, side: Side) = when (wing) {
    is BatWing -> visualizeWing(state, side, wing.color, ::createLeftBatWing)
    is BirdWing -> visualizeWing(state, side, wing.color, ::createLeftBirdWing)
    is ButterflyWing -> visualizeWing(state, side, wing.color, ::createLeftButterflyWing)
}

private fun visualizeWing(
    state: CharacterRenderState,
    side: Side,
    color: Color,
    createLeftWing: (CharacterRenderState) -> Polygon2d,
) {
    val options = FillAndBorder(color.toRender(), state.config.line)
    val layer = if (state.renderFront) {
        WING_LAYER
    } else {
        -WING_LAYER
    }

    var polygon = createLeftWing(state)

    if ((side == Side.Right && state.renderFront) ||
        (side == Side.Left && !state.renderFront)
    ) {
        polygon = state.aabb.mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createLeftBatWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = Factor(0.55f)

    builder.addLeftPoint(state.aabb, startX, Factor(0.3f), true)
    builder.addLeftPoint(state.aabb, Factor(0.8f), Factor(0.3f))
    builder.addLeftPoint(state.aabb, Factor(0.7f), START, true)
    builder.addLeftPoint(state.aabb, END, Factor(0.3f))
    builder.addLeftPoint(state.aabb, Factor(0.9f), END, true)
    builder.addLeftPoint(state.aabb, Factor(0.85f), Factor(0.6f))
    builder.addLeftPoint(state.aabb, Factor(0.7f), Factor(0.9f), true)
    builder.addLeftPoint(state.aabb, Factor(0.7f), Factor(0.6f))
    builder.addLeftPoint(state.aabb, startX, Factor(0.4f), true)

    val polygon = builder.build()
    return polygon
}

private fun createLeftBirdWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = Factor(0.55f)

    builder.addLeftPoint(state.aabb, startX, Factor(0.3f))
    builder.addLeftPoint(state.aabb, Factor(0.7f), START)
    builder.addLeftPoint(state.aabb, END, Factor(0.3f))
    builder.addLeftPoint(state.aabb, END, END)
    builder.addLeftPoint(state.aabb, Factor(0.9f), END)
    builder.addLeftPoint(state.aabb, startX, Factor(0.6f))

    val polygon = builder.build()
    return polygon
}

private fun createLeftButterflyWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = Factor(0.55f)
    val centerX = Factor(0.7f)

    builder.addLeftPoint(state.aabb, startX, CENTER)
    builder.addLeftPoint(state.aabb, startX, START)
    builder.addLeftPoint(state.aabb, END, START)
    builder.addLeftPoint(state.aabb, END, CENTER)
    builder.addLeftPoint(state.aabb, centerX, CENTER)
    builder.addLeftPoint(state.aabb, END, CENTER)
    builder.addLeftPoint(state.aabb, END, END)
    builder.addLeftPoint(state.aabb, startX, END)

    val polygon = builder.build()
    return polygon
}
