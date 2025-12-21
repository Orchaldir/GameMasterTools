package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeHorn(
    state: CharacterRenderState<Head>,
    horn: Horn,
    side: Side,
    skin: Skin,
    hair: Hair,
) {
    when (horn) {
        is SimpleHorn -> visualizeSimpleHorn(state, horn, side, skin, hair)
        is ComplexHorn -> visualizeComplexHorn(state, horn, side, skin, hair)
    }
}

fun visualizeSimpleHorn(
    state: CharacterRenderState<Head>,
    horn: SimpleHorn,
    side: Side,
    skin: Skin,
    hair: Hair,
) {
    val config = state.config.head.hornConfig
    val complex = when (horn.simpleType) {
        SimpleHornType.Gemsbok -> config.gemsbok
        SimpleHornType.Mouflon -> config.mouflon
        SimpleHornType.Saiga -> config.saiga
        SimpleHornType.WaterBuffalo -> config.waterBuffalo
    }.copy(length = horn.length, color = horn.color)

    visualizeComplexHorn(state, complex, side, skin, hair)
}

fun visualizeComplexHorn(
    state: CharacterRenderState<Head>,
    horn: ComplexHorn,
    side: Side,
    skin: Skin,
    hair: Hair,
) {
    val options = state.config.getFeatureOptions(state.state, horn.color, hair, skin)
    val layer = state.config.head.hornConfig.getLayer(state.renderFront)

    var polygon = createLeftHorn(state, horn)

    if ((side == Side.Right && state.renderFront) ||
        (side == Side.Left && !state.renderFront)
    ) {
        polygon = state.headAABB().mirrorVertically(polygon)
    }

    state.renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

private fun createLeftHorn(state: CharacterRenderState<Head>, horn: ComplexHorn): Polygon2d {
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
    state: CharacterRenderState<Head>,
    horn: ComplexHorn,
    builder: Polygon2dBuilder,
) {
    val y = state.config.head.hornConfig.y
    val halfWidth = horn.getWidth() / 2.0f

    builder.addLeftPoint(state.headAABB(), CENTER, y + halfWidth)
    builder.addRightPoint(state.headAABB(), CENTER, y - halfWidth)

    createLeftHornAtSide(state, horn, builder)
}

private fun createLeftHornInFront(
    state: CharacterRenderState<Head>,
    horn: ComplexHorn,
    builder: Polygon2dBuilder,
) {
    val x = fromPercentage(80)
    val halfWidth = horn.getWidth() / 2.0f
    val y = state.config.head.hornConfig.y + halfWidth

    builder.addRightPoint(state.headAABB(), x - halfWidth, y)
    builder.addLeftPoint(state.headAABB(), x + halfWidth, y)

    createLeftHornAtTop(state, horn, builder)
}

private fun createLeftHornAtSide(
    state: CharacterRenderState<Head>,
    horn: ComplexHorn,
    builder: Polygon2dBuilder,
) {
    val y = state.config.head.hornConfig.y
    val halfWidthFactor = horn.getWidth() / 2.0f

    builder.addLeftPoint(state.headAABB(), END, y + halfWidthFactor, true)
    builder.addRightPoint(state.headAABB(), END, y - halfWidthFactor, true)

    addShape(state, horn, builder, state.headAABB().getPoint(END, y), Orientation.zero())
}

private fun createLeftHornAtTop(
    state: CharacterRenderState<Head>,
    horn: ComplexHorn,
    builder: Polygon2dBuilder,
) {
    val x = fromPercentage(80)
    val halfWidth = horn.getWidth() / 2.0f

    builder.addRightPoint(state.headAABB(), x - halfWidth, START, true)
    builder.addLeftPoint(state.headAABB(), x + halfWidth, START, true)

    addShape(state, horn, builder, state.headAABB().getPoint(x, START), -QUARTER_CIRCLE)
}

private fun addShape(
    state: CharacterRenderState<Head>,
    horn: ComplexHorn,
    builder: Polygon2dBuilder,
    startPosition: Point2d,
    startOrientation: Orientation,
) {
    val halfWidthFactor = horn.getWidth() / 2.0f
    val aabb = state.headAABB()
    val length = aabb.convertHeight(horn.length)

    when (horn.shape) {
        is CurvedHorn -> {
            val steps = 10
            val stepLength = length / steps
            val stepOrientation = horn.shape.change / steps
            var orientation = startOrientation + horn.orientationOffset
            var center = startPosition
            var halfWidth = aabb.convertHeight(halfWidthFactor)
            val stepWidth = halfWidth / steps
            // todo: add option to use constant or linear decreasing

            repeat(steps - 1) {
                orientation += stepOrientation
                halfWidth -= stepWidth
                center = center.createPolar(stepLength, orientation)

                builder.addLeftAndRightPoint(center, orientation, halfWidth)
            }

            orientation += stepOrientation
            center = center.createPolar(stepLength, orientation)

            builder.addLeftPoint(center, true)
        }

        is StraightHorn -> {
            val end = startPosition.createPolar(length, startOrientation + horn.orientationOffset)
            builder.addLeftPoint(end, true)
        }

        is SpiralHorn -> {
            val orientation = startOrientation + horn.orientationOffset
            var center = startPosition
            var halfWidth = aabb.convertHeight(halfWidthFactor)
            val weightCalculator = LinearDecreasingWeight(horn.shape.cycles)
            var amplitude = length * horn.shape.amplitude
            var sideOfAmplitude = 1.0f
            val constantStep = 1.0f - 1.0f / horn.shape.cycles

            repeat(horn.shape.cycles) {
                val cycleDistance = length * weightCalculator.calculate(it)
                val halfOnCenterLine = center.createPolar(cycleDistance / 2, orientation)
                val halfCenter = halfOnCenterLine.createPolar(amplitude, orientation + QUARTER_CIRCLE * sideOfAmplitude)

                builder.addLeftAndRightPoint(halfCenter, orientation, halfWidth)

                sideOfAmplitude = -sideOfAmplitude
                amplitude *= constantStep
                halfWidth *= constantStep
                center = center.createPolar(cycleDistance, orientation)
            }

            builder.addLeftPoint(center, true)
        }
    }
}