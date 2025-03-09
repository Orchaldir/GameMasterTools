package at.orchaldir.gm.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.horn.CrownOfHorns
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.LineSplitter
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeCrownOfHorns(state: CharacterRenderState, crown: CrownOfHorns) {
    val pair = state.aabb.getMirroredPoints(FULL, state.config.head.hornConfig.y)

    renderLineOfHorns(pair, crown.front)
}

private fun renderLineOfHorns(
    pair: Pair<Point2d, Point2d>,
    horns: Int,
) {
    val frontSplitter = LineSplitter.fromStartAndEnd(pair, horns)

    frontSplitter.getCenters().forEach {

    }
}