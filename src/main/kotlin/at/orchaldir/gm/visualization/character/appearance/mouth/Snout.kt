package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.mouth.Snout
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeSnout(state: CharacterRenderState, snout: Snout) {
    if (!state.renderFront) {
        return
    }

    when (snout.shape) {
        SnoutShape.Cat -> doNothing()
        SnoutShape.Cow -> doNothing()
        SnoutShape.Dog -> visualizeCow(state, snout)
        SnoutShape.Pig -> doNothing()
    }
}

private fun visualizeCow(state: CharacterRenderState, snout: Snout) {
    val options = state.config.getLineOptions(snout.color)
    val upperY = Factor(0.6f)
    val lowerY = Factor(1.1f)
    val width = Factor(1.1f)
    val polygon = Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, upperY)
        .addMirroredPoints(state.aabb, width, upperY)
        .addMirroredPoints(state.aabb, width, (upperY + lowerY) / 2.0f)
        .addMirroredPoints(state.aabb, width, lowerY)
        .addLeftPoint(state.aabb, CENTER, lowerY)
        .build()
    state.renderer.getLayer()
        .renderRoundedPolygon(polygon, options)
}
