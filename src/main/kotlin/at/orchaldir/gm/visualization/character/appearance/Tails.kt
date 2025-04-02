package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class TailConfig(
    val bunnySize: SizeConfig<Factor>,
    val horseLength: Factor,
    val startY: Factor,
)

fun visualizeTails(state: CharacterRenderState, tails: Tails) = when (tails) {
    NoTails -> doNothing()
    is SimpleTail -> visualizeSimpleTail(state, tails)
}

private fun visualizeSimpleTail(state: CharacterRenderState, tail: SimpleTail) = when (tail.shape) {
    SimpleTailShape.Bunny -> visualizeBunny(state, tail)
    SimpleTailShape.Cat -> doNothing()
    SimpleTailShape.Horse -> visualizeHorse(state, tail)
    SimpleTailShape.Rat -> visualizeRat(state, tail)
    SimpleTailShape.Squirrel -> visualizeSquirrel(state, tail)
}

private fun visualizeBunny(state: CharacterRenderState, tail: SimpleTail) {
    val config = state.config.body.tail
    val center = state.aabb.getPoint(CENTER, config.startY)
    val radius = state.aabb.convertHeight(config.bunnySize.convert(tail.size))
    val options = state.config.getLineOptions(tail.color)

    state.getTailLayer().renderCircle(center, radius, options)
}

private fun visualizeHorse(state: CharacterRenderState, tail: SimpleTail) {
    val config = state.config.body.tail
    val radius = config.bunnySize.convert(tail.size)
    val width = radius * 2.0f
    val options = state.config.getLineOptions(tail.color)
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width, config.startY - radius)
        .addMirroredPoints(state.aabb, width * 0.9f, config.startY + config.horseLength / 2.0f)
        .addMirroredPoints(state.aabb, width, config.startY + config.horseLength, true)
        .build()

    state.getTailLayer().renderRoundedPolygon(polygon, options)
}

private fun visualizeRat(state: CharacterRenderState, tail: SimpleTail) {
    val options = state.config.getLineOptions(tail.color)
    val config = state.config.body.tail
    val line = Line2dBuilder()
        .addPoint(state.aabb, CENTER, config.startY)
        .addPoint(state.aabb, fromPercentage(30), config.startY)
        .addPoint(state.aabb, fromPercentage(30), config.startY + fromPercentage(30))
        .addPoint(state.aabb, fromPercentage(70), config.startY + fromPercentage(30))
        .addPoint(state.aabb, fromPercentage(70), config.startY - fromPercentage(20))
        .addPoint(state.aabb, fromPercentage(90), config.startY - fromPercentage(20))
        .build()
    val polygon = buildTail(line, fromMillimeters(100))

    state.getTailLayer().renderRoundedPolygon(polygon, options)
}

private fun visualizeSquirrel(state: CharacterRenderState, tail: SimpleTail) {
    val config = state.config.body.tail
    val options = state.config.getLineOptions(tail.color)
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

private fun buildTail(line: Line2d, width: Distance): Polygon2d {
    val half = width / 2.0f
    logger.info { "line=$line" }
    val subdivided = subdivideLine(line, 2)
    logger.info { "subdivided=$subdivided" }
    val builder = Polygon2dBuilder()

    subdivided.points.dropLast(1).withIndex().forEach { (index, center) ->
        val orientation = subdivided.calculateOrientation(index)
        logger.info { "index={$index} $center orientation=$orientation" }
        builder.addLeftAndRightPoint(center, orientation, half)
    }

    return builder
        .addLeftPoint(subdivided.points.last())
        .build()
}