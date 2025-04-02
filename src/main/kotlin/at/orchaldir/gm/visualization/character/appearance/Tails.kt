package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.tail.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class TailConfig(
    val bunnySize: SizeConfig<Factor>,
    val horseLength: Factor,
    val startY: Factor,
)

fun visualizeTails(state: CharacterRenderState, tails: Tails, skin: Skin, hair: Hair) = when (tails) {
    NoTails -> doNothing()
    is SimpleTail -> visualizeSimpleTail(state, tails, skin, hair)
}

private fun visualizeSimpleTail(state: CharacterRenderState, tail: SimpleTail, skin: Skin, hair: Hair) {
    val options = when (tail.color) {
        is OverwriteTailColor -> state.config.getLineOptions(tail.color.color)
        ReuseHairColor -> when (hair) {
            NoHair -> error("Cannot reuse hair color without hair!")
            is NormalHair -> state.config.getLineOptions(hair.color)
        }

        ReuseSkinColor -> state.config.getOptions(skin)
    }

    when (tail.shape) {
        SimpleTailShape.Bunny -> visualizeBunny(state, options, tail)
        SimpleTailShape.Cat -> visualizeCat(state, options, tail)
        SimpleTailShape.Horse -> visualizeHorse(state, options, tail)
        SimpleTailShape.Rat -> visualizeRat(state, options, tail)
        SimpleTailShape.Squirrel -> visualizeSquirrel(state, options)
    }
}

private fun visualizeBunny(state: CharacterRenderState, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val center = state.aabb.getPoint(CENTER, config.startY)
    val radius = state.aabb.convertHeight(config.bunnySize.convert(tail.size))

    state.getTailLayer().renderCircle(center, radius, options)
}

private fun visualizeCat(state: CharacterRenderState, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val line = createTailLine(state, config)
    val polygon = buildTailPolygon(line, fromMillimeters(100), false)

    renderTailPolygon(state, options, polygon)
}

private fun visualizeHorse(state: CharacterRenderState, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val radius = config.bunnySize.convert(tail.size)
    val width = radius * 2.0f
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width, config.startY - radius)
        .addMirroredPoints(state.aabb, width * 0.9f, config.startY + config.horseLength / 2.0f)
        .addMirroredPoints(state.aabb, width, config.startY + config.horseLength, true)
        .build()

    state.getTailLayer().renderRoundedPolygon(polygon, options)
}

private fun visualizeRat(state: CharacterRenderState, options: RenderOptions, tail: SimpleTail) {
    val config = state.config.body.tail
    val line = createTailLine(state, config)
    val polygon = buildTailPolygon(line, fromMillimeters(150), true)

    renderTailPolygon(state, options, polygon)
    //state.getTailLayer().renderLine(line.points, LineOptions(Color.Red.toRender(), 0.02f))
}

private fun visualizeSquirrel(state: CharacterRenderState, options: RenderOptions) {
    val config = state.config.body.tail
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, fromPercentage(50), fromPercentage(80))
        .addMirroredPoints(state.aabb, fromPercentage(50), fromPercentage(10))
        .addLeftPoint(state.aabb, CENTER, fromPercentage(10))
        .build()

    state.getTailLayer().renderRoundedPolygon(polygon, options)

    if (!state.renderFront) {
        val backPolygon = Polygon2dBuilder()
            .addLeftPoint(state.aabb, CENTER, fromPercentage(10))
            .addMirroredPoints(state.aabb, fromPercentage(45), fromPercentage(10))
            .addMirroredPoints(state.aabb, fromPercentage(45), fromPercentage(30))
            .addMirroredPoints(state.aabb, fromPercentage(20), fromPercentage(40))
            .build()

        state.getTailLayer().renderRoundedPolygon(backPolygon, options)
    }
}

private fun createTailLine(
    state: CharacterRenderState,
    config: TailConfig,
) = Line2dBuilder()
    .addPoint(state.aabb, CENTER, config.startY)
    .addPoint(state.aabb, fromPercentage(30), config.startY + fromPercentage(5))
    .addPoint(state.aabb, fromPercentage(35), config.startY + fromPercentage(30))
    .addPoint(state.aabb, fromPercentage(75), config.startY + fromPercentage(30))
    .addPoint(state.aabb, fromPercentage(70), config.startY - fromPercentage(20))
    .addPoint(state.aabb, fromPercentage(90), config.startY - fromPercentage(25))
    .build()

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
    state: CharacterRenderState,
    options: RenderOptions,
    polygon: Polygon2d,
) {
    val mirrored = if (!state.renderFront) {
        polygon
    } else {
        state.aabb.mirrorVertically(polygon)
    }

    state.getTailLayer().renderRoundedPolygon(mirrored, options)
}