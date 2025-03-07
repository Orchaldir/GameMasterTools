package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeHorns(state: CharacterRenderState, horns: Horns) = when (horns) {
    NoHorns -> doNothing()
    is TwoHorns -> {
        visualizeHorn(state, horns.horn, Side.Left)
        visualizeHorn(state, horns.horn, Side.Right)
    }

    is DifferentHorns -> {
        visualizeHorn(state, horns.left, Side.Left)
        visualizeHorn(state, horns.right, Side.Right)
    }
}

private fun visualizeHorn(state: CharacterRenderState, horn: Horn, side: Side) = when (horn) {
    is CurvedHorn -> visualizeCurvedHorn(state, side, horn)
}

private fun visualizeCurvedHorn(
    state: CharacterRenderState,
    side: Side,
    horn: CurvedHorn,
) {
    val options = FillAndBorder(horn.color.toRender(), state.config.line)
    val layer = if (state.renderFront) {
        -WING_LAYER
    } else {
        WING_LAYER
    }

    var polygon = createLeftCurvedHorn(state, horn)

    if ((side == Side.Right && state.renderFront) ||
        (side == Side.Left && !state.renderFront)
    ) {
        polygon = state.aabb.mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createLeftCurvedHorn(state: CharacterRenderState, horn: CurvedHorn): Polygon2d {
    val builder = Polygon2dBuilder()

    when (horn.position) {
        HornPosition.Brow -> createLeftCurvedHornAtBrow(state, horn, builder)
        HornPosition.Front -> createLeftCurvedHornInFront(state, horn, builder)
        HornPosition.Side -> createLeftCurvedHornAtSide(state, horn, builder)
        HornPosition.Top -> createLeftCurvedHornAtTop(state, horn, builder)
    }

    val polygon = builder.build()
    return polygon
}

private fun createLeftCurvedHornAtBrow(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val y = Factor(0.2f)
    val halfWidth = horn.width / 2.0f

    createLeftCurvedHornAtSide(state, horn, builder)

    builder.addPoint(state.aabb, CENTER, y - halfWidth)
    builder.addPoint(state.aabb, CENTER, y + halfWidth)
}

private fun createLeftCurvedHornInFront(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val x = Factor(0.8f)
    val y = Factor(0.3f)
    val halfWidth = horn.width / 2.0f

    createLeftCurvedHornAtTop(state, horn, builder)

    builder.addPoint(state.aabb, x - halfWidth, y)
    builder.addPoint(state.aabb, x + halfWidth, y)
}

private fun createLeftCurvedHornAtSide(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val y = Factor(0.2f)
    val halfWidthFactor = horn.width / 2.0f
    val start = state.aabb.getPoint(END, y)
    val length = state.aabb.convertHeight(horn.length)

    builder.addPoint(state.aabb, END, y + halfWidthFactor, true)

    when (horn.curve) {
        is ConstantCurvature -> {
            val steps = 10
            val stepLength = length / steps
            val stepOrientation = horn.curve.change / steps
            var orientation = Orientation.zero()
            var center = start
            var halfWidth = state.aabb.convertHeight(halfWidthFactor)
            val stepWidth = halfWidth / steps

            repeat(steps) {
                val right = center.createPolar(halfWidth, orientation + QUARTER)
                builder.addPoint(right)

                val left = center.createPolar(halfWidth, orientation - QUARTER)
                builder.addPoint(left)

                halfWidth -= stepWidth
                center = center.createPolar(stepLength, orientation)
                orientation += stepOrientation
            }

            builder.addPoint(center, true)
        }

        is StraightHorn -> {
            val end = start.createPolar(length, horn.curve.orientation)
            builder.addPoint(end, true)
        }
    }

    builder.addPoint(state.aabb, END, y - halfWidthFactor, true)
}

private fun createLeftCurvedHornAtTop(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val x = Factor(0.8f)
    val halfWidth = horn.width / 2.0f

    builder.addPoint(state.aabb, x + halfWidth, START, true)
    builder.addPoint(state.aabb, x, START - horn.length, true)
    builder.addPoint(state.aabb, x - halfWidth, START, true)
}