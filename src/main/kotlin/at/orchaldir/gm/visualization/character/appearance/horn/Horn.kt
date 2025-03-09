package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeHorn(
    state: CharacterRenderState,
    horn: Horn,
    side: Side,
) {
    val options = FillAndBorder(horn.color.toRender(), state.config.line)
    val layer = state.config.head.hornConfig.getLayer(state.renderFront)

    var polygon = createLeftHorn(state, horn)

    if ((side == Side.Right && state.renderFront) ||
        (side == Side.Left && !state.renderFront)
    ) {
        polygon = state.aabb.mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createLeftHorn(state: CharacterRenderState, horn: Horn): Polygon2d {
    val builder = Polygon2dBuilder()

    when (horn.position) {
        HornPosition.Brow -> createLeftHornAtBrow(state, horn, builder)
        HornPosition.Front -> createLeftHornInFront(state, horn, builder)
        HornPosition.Side -> createLeftHornAtSide(state, horn, builder)
        HornPosition.Top -> createLeftHornAtTop(state, horn, builder)
    }

    return builder.build()
}

private fun createLeftHornAtBrow(
    state: CharacterRenderState,
    horn: Horn,
    builder: Polygon2dBuilder,
) {
    val y = state.config.head.hornConfig.y
    val halfWidth = horn.width / 2.0f

    builder.addLeftPoint(state.aabb, CENTER, y + halfWidth)
    builder.addRightPoint(state.aabb, CENTER, y - halfWidth)

    createLeftHornAtSide(state, horn, builder)
}

private fun createLeftHornInFront(
    state: CharacterRenderState,
    horn: Horn,
    builder: Polygon2dBuilder,
) {
    val x = Factor(0.8f)
    val halfWidth = horn.width / 2.0f
    val y = state.config.head.hornConfig.y + halfWidth

    builder.addRightPoint(state.aabb, x - halfWidth, y)
    builder.addLeftPoint(state.aabb, x + halfWidth, y)

    createLeftHornAtTop(state, horn, builder)
}

private fun createLeftHornAtSide(
    state: CharacterRenderState,
    horn: Horn,
    builder: Polygon2dBuilder,
) {
    val y = state.config.head.hornConfig.y
    val halfWidthFactor = horn.width / 2.0f

    builder.addLeftPoint(state.aabb, END, y + halfWidthFactor, true)
    builder.addRightPoint(state.aabb, END, y - halfWidthFactor, true)

    addCurve(state, horn, builder, state.aabb.getPoint(END, y), Orientation.zero())
}

private fun createLeftHornAtTop(
    state: CharacterRenderState,
    horn: Horn,
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
    horn: Horn,
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