package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class WingsConfig(
    private val diameter: SizeConfig<Factor>,
    private val distanceBetweenWings: SizeConfig<Factor>,
    private val almondHeight: Factor,
    private val ellipseHeight: Factor,
    val pupilFactor: Factor,
    val slitFactor: Factor,
)

fun visualizeWings(state: CharacterRenderState, wings: Wings) = when (wings) {
    NoWings -> doNothing()
    is OneWing -> visualizeWing(state, wings.wing, wings.side)
    is TwoWings -> {
        visualizeWing(state, wings.wing, Side.Left)
        visualizeWing(state, wings.wing, Side.Right)
    }

    is DifferentWings -> {
        visualizeWing(state, wings.left, Side.Left)
        visualizeWing(state, wings.right, Side.Right)
    }
}

private fun visualizeWing(state: CharacterRenderState, wing: Wing, side: Side) = when (wing) {
    is BatWing -> doNothing()
    is BirdWing -> doNothing()
    is ButterflyWing -> visualizeButterflyWing(state, wing, side)
}

private fun visualizeButterflyWing(state: CharacterRenderState, wing: ButterflyWing, side: Side) {
    if (side == Side.Right) {

    }
}
