package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig

data class TailConfig(
    val bunnySize: SizeConfig<Factor>,
    val horseLength: Factor,
    val startY: Factor,
) {
    fun getBunnyCenter(config: ICharacterConfig<Body>) = config.fullAABB()
        .getPoint(CENTER, startY)

    fun getBunnyRadius(config: ICharacterConfig<Body>, size: Size) = config.fullAABB()
        .convertHeight(bunnySize.convert(size))
}

fun visualizeTails(state: CharacterRenderState<Body>, tails: Tails, skin: Skin, hair: Hair) = when (tails) {
    NoTails -> doNothing()
    is SimpleTail -> visualizeSimpleTail(state, tails, skin, hair)
}

private fun visualizeSimpleTail(state: CharacterRenderState<Body>, tail: SimpleTail, skin: Skin, hair: Hair) {
    val options = state.config.getFeatureOptions(state.state, tail.color, hair, skin)

    when (tail.shape) {
        SimpleTailShape.Bunny -> visualizeBunny(state, options, tail)
        SimpleTailShape.Cat -> visualizeCat(state, options, tail)
        SimpleTailShape.Horse -> visualizeHorse(state, options, tail)
        SimpleTailShape.Rat -> visualizeRat(state, options, tail)
        SimpleTailShape.Squirrel -> visualizeSquirrel(state, options)
    }
}

private fun visualizeBunny(state: CharacterRenderState<Body>, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val center = config.getBunnyCenter(state)
    val radius = config.getBunnyRadius(state, tail.size)

    state.getTailLayer().renderCircle(center, radius, options)
}

private fun visualizeCat(state: CharacterRenderState<Body>, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val line = createTailLine(state, config)
    val radius = config.getBunnyRadius(state, tail.size)
    val polygon = buildTailPolygon(line, radius, false)

    renderTailPolygon(state, options, polygon)
}

private fun visualizeHorse(state: CharacterRenderState<Body>, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val radius = config.bunnySize.convert(tail.size)
    val width = radius * 2.0f
    val aabb = state.fullAABB()
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, width, config.startY - radius)
        .addMirroredPoints(aabb, width * 0.9f, config.startY + config.horseLength / 2.0f)
        .addMirroredPoints(aabb, width, config.startY + config.horseLength, true)
        .build()

    state.getTailLayer().renderRoundedPolygon(polygon, options)
}

private fun visualizeRat(state: CharacterRenderState<Body>, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val line = createTailLine(state, config)
    val radius = config.getBunnyRadius(state, tail.size) * 1.5f
    val polygon = buildTailPolygon(line, radius, true)

    renderTailPolygon(state, options, polygon)
}

private fun visualizeSquirrel(state: CharacterRenderState<Body>, options: RenderOptions) {
    val aabb = state.fullAABB()
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(aabb, fromPercentage(50), fromPercentage(80))
        .addMirroredPoints(aabb, fromPercentage(50), fromPercentage(10))
        .addLeftPoint(aabb, CENTER, fromPercentage(10))
        .build()

    state.getTailLayer().renderRoundedPolygon(polygon, options)

    if (!state.renderFront) {
        val backPolygon = Polygon2dBuilder()
            .addLeftPoint(aabb, CENTER, fromPercentage(10))
            .addMirroredPoints(aabb, fromPercentage(45), fromPercentage(10))
            .addMirroredPoints(aabb, fromPercentage(45), fromPercentage(30))
            .addMirroredPoints(aabb, fromPercentage(20), fromPercentage(40))
            .build()

        state.getTailLayer().renderRoundedPolygon(backPolygon, options)
    }
}

private fun createTailLine(
    state: CharacterRenderState<Body>,
    config: TailConfig,
): Line2d {
    val aabb = state.fullAABB()

    return Line2dBuilder()
        .addPoint(aabb, CENTER, config.startY)
        .addPoint(aabb, fromPercentage(30), config.startY + fromPercentage(5))
        .addPoint(aabb, fromPercentage(35), config.startY + fromPercentage(30))
        .addPoint(aabb, fromPercentage(75), config.startY + fromPercentage(30))
        .addPoint(aabb, fromPercentage(70), config.startY - fromPercentage(20))
        .addPoint(aabb, fromPercentage(90), config.startY - fromPercentage(25))
        .build()
}

private fun buildTailPolygon(line: Line2d, width: Distance, isSharp: Boolean): Polygon2d {
    val half = width / 2.0f
    val subdivided = subdivideLine(line, 2)
    val builder = Polygon2dBuilder()
    val size = subdivided.points.lastIndex

    subdivided.points.dropLast(1).withIndex().forEach { (index, center) ->
        val orientation = subdivided.calculateOrientation(index)
        val segmentHalf = if (isSharp) {
            val widthFactor = Factor.fromNumber((size - index) / size.toFloat())
            half * widthFactor
        } else {
            half
        }
        builder.addLeftAndRightPoint(center, orientation, segmentHalf)
    }

    return builder
        .addLeftPoint(subdivided.points.last())
        .build()
}

private fun renderTailPolygon(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    polygon: Polygon2d,
) {
    val mirrored = if (!state.renderFront) {
        polygon
    } else {
        state.fullAABB().mirrorVertically(polygon)
    }

    state.getTailLayer().renderRoundedPolygon(mirrored, options)
}