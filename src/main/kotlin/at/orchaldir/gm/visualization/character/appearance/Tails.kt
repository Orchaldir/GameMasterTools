package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Polygon2dBuilder
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
    SimpleTailShape.Rat -> doNothing()
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
    val width = config.bunnySize.convert(tail.size) * 2.0f
    val options = state.config.getLineOptions(tail.color)
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width, config.startY)
        .addMirroredPoints(state.aabb, width * 0.9f, config.startY + config.horseLength / 2.0f)
        .addMirroredPoints(state.aabb, width, config.startY + config.horseLength, true)
        .build()

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
            .addMirroredPoints(state.aabb, fromPercentage(40), fromPercentage(10))
            .addMirroredPoints(state.aabb, fromPercentage(20), fromPercentage(40))
            .build()

        state.getTailLayer().renderRoundedPolygon(backPolygon, options)
    }
}