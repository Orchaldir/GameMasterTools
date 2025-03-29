package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTail
import at.orchaldir.gm.core.model.character.appearance.tail.SimpleTailShape
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class TailConfig(
    val bunnySize: SizeConfig<Factor>,
    val y: Factor,
)

fun visualizeTails(state: CharacterRenderState, tails: Tails) = when (tails) {
    NoTails -> doNothing()
    is SimpleTail -> visualizeSimpleTail(state, tails)
}

private fun visualizeSimpleTail(state: CharacterRenderState, tail: SimpleTail) = when (tail.shape) {
    SimpleTailShape.Bunny -> visualizeBunnyTail(state, tail)
    SimpleTailShape.Cat -> doNothing()
    SimpleTailShape.Horse -> doNothing()
    SimpleTailShape.Rat -> doNothing()
    SimpleTailShape.Squirrel -> doNothing()
}

private fun visualizeBunnyTail(state: CharacterRenderState, tail: SimpleTail) {
    val config = state.config.body.tail
    val center = state.aabb.getPoint(CENTER, config.y)
    val radius = state.aabb.convertHeight(config.bunnySize.convert(tail.size))
    val options = state.config.getLineOptions(tail.fill)

    state.getTailLayer().renderCircle(center, radius, options)
}