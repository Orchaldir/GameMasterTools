package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.WING_LAYER

fun visualizeHorn(state: CharacterRenderState, horn: Horn, side: Side) = when (horn) {
    is CurvedHorn -> visualizeCurvedHorn(state, side, horn)
}

private fun visualizeCurvedHorn(
    state: CharacterRenderState,
    side: Side,
    horn: CurvedHorn,
) {
    val options = FillAndBorder(horn.color.toRender(), state.config.line)
    val layer = state.config.head.hornConfig.getLayer(state.renderFront)

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

    return builder.build()
}

private fun createLeftCurvedHornAtBrow(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val y = state.config.head.hornConfig.y
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
    val halfWidth = horn.width / 2.0f
    val y = state.config.head.hornConfig.y + halfWidth

    builder.addRightPoint(state.aabb, x - halfWidth, y)
    builder.addLeftPoint(state.aabb, x + halfWidth, y)

    createLeftCurvedHornAtTop(state, horn, builder)
}

private fun createLeftCurvedHornAtSide(
    state: CharacterRenderState,
    horn: CurvedHorn,
    builder: Polygon2dBuilder,
) {
    val y = state.config.head.hornConfig.y
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
            // todo: add option to use constant or linear decreasing

            repeat(steps - 1) {
                orientation += stepOrientation
                halfWidth -= stepWidth
                center = center.createPolar(stepLength, orientation)

                addLeftAndRight(builder, center, orientation, halfWidth)
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
            val weightCalculator = LinearDecreasingWeight(horn.curve.cycles)
            var amplitude = length * horn.curve.amplitude
            var sideOfAmplitude = 1.0f
            val constantStep = 1.0f - 1.0f / horn.curve.cycles

            repeat(horn.curve.cycles) {
                val cycleDistance = length * weightCalculator.calculate(it)
                val halfOnCenterLine = center.createPolar(cycleDistance / 2, orientation)
                val halfCenter = halfOnCenterLine.createPolar(amplitude, orientation + QUARTER * sideOfAmplitude)

                addLeftAndRight(builder, halfCenter, orientation, halfWidth)

                sideOfAmplitude = -sideOfAmplitude
                amplitude *= constantStep
                halfWidth *= constantStep
                center = center.createPolar(cycleDistance, orientation)
            }

            builder.addLeftPoint(center, true)
        }
    }
}

private fun addLeftAndRight(
    builder: Polygon2dBuilder,
    center: Point2d,
    orientation: Orientation,
    halfWidth: Distance,
) {
    val right = center.createPolar(halfWidth, orientation - QUARTER)
    val left = center.createPolar(halfWidth, orientation + QUARTER)

    builder.addPoints(left, right)
}