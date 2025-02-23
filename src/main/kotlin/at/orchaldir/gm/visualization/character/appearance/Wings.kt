package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class WingsConfig(
    private val diameter: SizeConfig<Factor>,
    private val distanceBetweenWings: SizeConfig<Factor>,
    private val almondHeight: Factor,
    private val ellipseHeight: Factor,
    val pupilFactor: Factor,
    val slitFactor: Factor,
)

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
    is BatWing -> visualizeWing(state, side, wing.color, ::createRightBatWing)
    is BirdWing -> visualizeWing(state, side, wing.color, ::createRightBirdWing)
    is ButterflyWing -> visualizeWing(state, side, wing.color, ::createRightButterflyWing)
}

private fun visualizeWing(
    state: CharacterRenderState,
    side: Side,
    color: Color,
    createPolygon: (CharacterRenderState) -> Polygon2d,
) {
    val options = FillAndBorder(color.toRender(), state.config.line)
    val layer = if (state.renderFront) {
        WING_LAYER
    } else {
        -WING_LAYER
    }

    var polygon = createPolygon(state)

    if (side == Side.Left) {
        polygon = state.aabb.mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createRightBatWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = Factor(0.55f)

    builder.addPoint(state.aabb, startX, Factor(0.3f), true)
    builder.addPoint(state.aabb, Factor(0.8f), Factor(0.3f))
    builder.addPoint(state.aabb, Factor(0.7f), START, true)
    builder.addPoint(state.aabb, END, Factor(0.3f))
    builder.addPoint(state.aabb, Factor(0.9f), END, true)
    builder.addPoint(state.aabb, Factor(0.85f), Factor(0.6f))
    builder.addPoint(state.aabb, Factor(0.7f), Factor(0.9f), true)
    builder.addPoint(state.aabb, Factor(0.7f), Factor(0.6f))
    builder.addPoint(state.aabb, startX, Factor(0.4f), true)

    val polygon = builder.build()
    return polygon
}

private fun createRightBirdWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = Factor(0.55f)

    builder.addPoint(state.aabb, startX, Factor(0.3f))
    builder.addPoint(state.aabb, Factor(0.7f), START)
    builder.addPoint(state.aabb, END, Factor(0.3f))
    builder.addPoint(state.aabb, END, END)
    builder.addPoint(state.aabb, Factor(0.9f), END)
    builder.addPoint(state.aabb, startX, Factor(0.6f))

    val polygon = builder.build()
    return polygon
}

private fun createRightButterflyWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = Factor(0.55f)
    val centerX = Factor(0.7f)

    builder.addPoint(state.aabb, startX, CENTER)
    builder.addPoint(state.aabb, startX, START)
    builder.addPoint(state.aabb, END, START)
    builder.addPoint(state.aabb, END, CENTER)
    builder.addPoint(state.aabb, centerX, CENTER)
    builder.addPoint(state.aabb, END, CENTER)
    builder.addPoint(state.aabb, END, END)
    builder.addPoint(state.aabb, startX, END)

    val polygon = builder.build()
    return polygon
}
