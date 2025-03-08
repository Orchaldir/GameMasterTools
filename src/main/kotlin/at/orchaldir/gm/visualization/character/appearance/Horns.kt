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

    is CrownOfHorns -> doNothing()
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

    builder.addLeftPoint(state.aabb, CENTER, y + halfWidth)
    builder.addRightPoint(state.aabb, CENTER, y - halfWidth)

    createLeftCurvedHornAtSide(state, horn, builder)
}

private fun createLeftCurvedHornInFront(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val x = Factor(0.8f)
    val y = Factor(0.3f)
    val halfWidth = horn.width / 2.0f

    builder.addRightPoint(state.aabb, x - halfWidth, y)
    builder.addLeftPoint(state.aabb, x + halfWidth, y)

    createLeftCurvedHornAtTop(state, horn, builder)
}

private fun createLeftCurvedHornAtSide(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val y = Factor(0.2f)
    val halfWidthFactor = horn.width / 2.0f

    builder.addLeftPoint(state.aabb, END, y + halfWidthFactor, true)
    builder.addRightPoint(state.aabb, END, y - halfWidthFactor, true)

    addCurve(state, horn, builder, state.aabb.getPoint(END, y), Orientation.zero())
}

private fun createLeftCurvedHornAtTop(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val x = Factor(0.8f)
    val halfWidth = horn.width / 2.0f

    builder.addRightPoint(state.aabb, x - halfWidth, START, true)
    builder.addLeftPoint(state.aabb, x + halfWidth, START, true)

    addCurve(state, horn, builder, state.aabb.getPoint(x, START), -QUARTER)
}

private fun addCurve(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
    startPosition: Point2d,
    startOrientation: Orientation,
) {
    val halfWidthFactor = horn.width / 2.0f
    val length = state.aabb.convertHeight(horn.length)

    when (horn.curve) {
        is ConstantCurvature -> {
            val steps = 10
            val stepLength = length / steps
            val stepOrientation = horn.curve.change / steps
            var orientation = startOrientation + horn.orientationOffset
            var center = startPosition
            var halfWidth = state.aabb.convertHeight(halfWidthFactor)
            val stepWidth = halfWidth / steps

            repeat(steps - 1) {
                orientation += stepOrientation
                halfWidth -= stepWidth
                center = center.createPolar(stepLength, orientation)

                addLeftAndRight(builder, center, halfWidth, orientation)
            }

            builder.addLeftPoint(center, true)
        }

        is StraightHorn -> {
            val end = startPosition.createPolar(length, startOrientation + horn.orientationOffset)
            builder.addLeftPoint(end, true)
        }

        is WaveCurve -> {
            val orientation = startOrientation + horn.orientationOffset
            var center = startPosition
            var halfWidth = state.aabb.convertHeight(halfWidthFactor)
            val totalWeight = (0..<horn.curve.cycles).sumOf { it + 2 }.toFloat()
            var halfOffset = length * horn.curve.amplitude
            var side = 1.0f
            val step = 1.0f - 1.0f / horn.curve.cycles

            repeat(horn.curve.cycles) {
                val weight = (horn.curve.cycles - it + 1) / totalWeight
                val cycleDistance = length * weight
                val halfOnCenterLine = center.createPolar(cycleDistance / 2, orientation)
                val halfCenter = halfOnCenterLine.createPolar(halfOffset, orientation + QUARTER * side)

                addLeftAndRight(builder, halfCenter, halfWidth, orientation)

                side = -side
                halfOffset *= step
                halfWidth *= step
                center = center.createPolar(cycleDistance, orientation)
            }

            builder.addLeftPoint(center, true)
        }
    }
}

private fun addLeftAndRight(
    builder: Polygon2dBuilder,
    center: Point2d,
    halfWidth: Distance,
    orientation: Orientation,
) {
    val right = center.createPolar(halfWidth, orientation - QUARTER)
    val left = center.createPolar(halfWidth, orientation + QUARTER)

    builder.addPoints(left, right)
}