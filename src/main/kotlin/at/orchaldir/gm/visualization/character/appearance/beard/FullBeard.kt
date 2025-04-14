package at.orchaldir.gm.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.beard.FullBeardStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeFullBeard(
    state: CharacterRenderState,
    style: FullBeardStyle,
    length: HairLength,
    color: Color,
) {
    val layer = state.getBeardLayer()
    val options = state.config.getLineOptions(color)
    val distance = state.config.getHairLength(state.aabb, length)
    val polygon = when (style) {
        FullBeardStyle.Forked -> return
        FullBeardStyle.Rectangle -> getRectangle(state, distance)
        FullBeardStyle.Triangle -> getTriangle(state, distance)
        FullBeardStyle.Wide -> return
    }

    layer.renderRoundedPolygon(polygon, options)
}

private fun getRectangle(state: CharacterRenderState, distance: Distance): Polygon2d {
    val padding = state.config.head.hair.longPadding
    val width = FULL + padding * 2.0f
    val startY = HALF
    val (left, right) = state.aabb.getMirroredPoints(width, FULL)

    return Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, startY)
        .addMirroredPoints(state.aabb, width, startY)
        .addPoints(left, right)
        .addPoints(left.addHeight(distance), right.addHeight(distance))
        .build()
}

private fun getTriangle(state: CharacterRenderState, distance: Distance): Polygon2d {
    val padding = state.config.head.hair.longPadding
    val width = FULL + padding * 2.0f
    val startY = HALF
    val center = state.aabb.getPoint(CENTER, FULL)

    return Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, startY)
        .addMirroredPoints(state.aabb, width, startY)
        .addMirroredPoints(state.aabb, width, END)
        .addLeftPoint(center.addHeight(distance))
        .build()
}