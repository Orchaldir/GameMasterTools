package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeWings(
    state: CharacterRenderState,
    wings: Wings,
    skin: Skin,
    hair: Hair,
) = when (wings) {
    NoWings -> doNothing()
    is OneWing -> visualizeWing(state, wings.wing, wings.side, skin, hair)
    is TwoWings -> {
        visualizeWing(state, wings.wing, Side.Left, skin, hair)
        visualizeWing(state, wings.wing, Side.Right, skin, hair)
    }

    is DifferentWings -> {
        visualizeWing(state, wings.left, Side.Left, skin, hair)
        visualizeWing(state, wings.right, Side.Right, skin, hair)
    }
}

private fun visualizeWing(
    state: CharacterRenderState,
    wing: Wing,
    side: Side,
    skin: Skin,
    hair: Hair,
) = when (wing) {
    is BatWing -> {
        val options = state.config.getFeatureOptions(state.state, wing.color, hair, skin)
        visualizeWing(state, side, options, ::createLeftBatWing)
    }

    is BirdWing -> visualizeWing(state, side, wing.color, ::createLeftBirdWing)
    is ButterflyWing -> visualizeWing(state, side, wing.color, ::createLeftButterflyWing)
}

private fun visualizeWing(
    state: CharacterRenderState,
    side: Side,
    color: Color,
    createLeftWing: (CharacterRenderState) -> Polygon2d,
) {
    val options = state.config.getLineOptions(color)

    visualizeWing(state, side, options, createLeftWing)
}

private fun visualizeWing(
    state: CharacterRenderState,
    side: Side,
    options: RenderOptions,
    createLeftWing: (CharacterRenderState) -> Polygon2d,
) {
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
    val startX = fromPercentage(55)

    builder.addLeftPoint(state.aabb, startX, fromPercentage(30), true)
    builder.addLeftPoint(state.aabb, fromPercentage(80), fromPercentage(30))
    builder.addLeftPoint(state.aabb, fromPercentage(70), START, true)
    builder.addLeftPoint(state.aabb, END, fromPercentage(30))
    builder.addLeftPoint(state.aabb, fromPercentage(90), END, true)
    builder.addLeftPoint(state.aabb, fromPercentage(85), fromPercentage(60))
    builder.addLeftPoint(state.aabb, fromPercentage(70), fromPercentage(90), true)
    builder.addLeftPoint(state.aabb, fromPercentage(70), fromPercentage(60))
    builder.addLeftPoint(state.aabb, startX, fromPercentage(40), true)

    val polygon = builder.build()
    return polygon
}

private fun createLeftBirdWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = fromPercentage(55)

    builder.addLeftPoint(state.aabb, startX, fromPercentage(30))
    builder.addLeftPoint(state.aabb, fromPercentage(70), START)
    builder.addLeftPoint(state.aabb, END, fromPercentage(30))
    builder.addLeftPoint(state.aabb, END, END)
    builder.addLeftPoint(state.aabb, fromPercentage(90), END)
    builder.addLeftPoint(state.aabb, startX, fromPercentage(60))

    val polygon = builder.build()
    return polygon
}

private fun createLeftButterflyWing(state: CharacterRenderState): Polygon2d {
    val builder = Polygon2dBuilder()
    val startX = fromPercentage(55)
    val centerX = fromPercentage(70)

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
