package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.Ponytail
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailPosition
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailPosition.BothSides
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailPosition.Right
import at.orchaldir.gm.core.model.character.appearance.hair.PonytailStyle
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.WING_LAYER
import at.orchaldir.gm.visualization.renderRoundedPolygon

fun visualizePonytail(state: CharacterRenderState, hair: NormalHair, ponytail: Ponytail) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val height = config.getHairLength(state.aabb, ponytail.length)
    val layer = state.getLayerIndex(WING_LAYER)
    val y = Factor.fromPercentage(20)

    val polygon = when (ponytail.position) {
        PonytailPosition.High -> getCenterPonytail(state, ponytail.style, height, y)
        PonytailPosition.Low -> getCenterPonytail(state, ponytail.style, height, FULL - y)
        else -> return //getLeftPonytail(state, ponytail.style, height, y)
    }

    if (ponytail.position != Right) {
        renderRoundedPolygon(state.renderer, options, polygon, layer)
    }

    if (ponytail.position == Right || ponytail.position == BothSides) {
        val right = state.aabb.mirrorVertically(polygon)
        renderRoundedPolygon(state.renderer, options, right, layer)
    }
}

private fun getCenterPonytail(
    state: CharacterRenderState,
    style: PonytailStyle,
    length: Distance,
    y: Factor,
): Polygon2d {
    val config = state.config.head.hair
    val (left, right) = state.aabb.getMirroredPoints(config.ponytailWidth, FULL)

    return Polygon2dBuilder()
        .addMirroredPoints(state.aabb, config.ponytailWidth, y)
        .addPoints(left.addHeight(length), right.addHeight(length))
        .build()
}

private fun getLeftPonytail(
    state: CharacterRenderState,
    style: PonytailStyle,
    length: Distance,
    y: Factor,
): Polygon2d {
    return Polygon2dBuilder()
        .build()
}
