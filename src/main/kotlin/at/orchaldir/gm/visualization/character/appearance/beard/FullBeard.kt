package at.orchaldir.gm.visualization.character.appearance.beard

import at.orchaldir.gm.core.model.character.appearance.beard.FullBeardStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.util.render.Color
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
        FullBeardStyle.Forked -> getFork(state, distance, HALF)
        FullBeardStyle.ForkedSide -> getFork(state, distance, FULL)
        FullBeardStyle.Rectangle -> getRectangle(state, distance, FULL)
        FullBeardStyle.Triangle -> getTriangle(state, distance)
        FullBeardStyle.Wide -> getRectangle(state, distance, state.config.head.beard.wideFullBeardWidth)
    }

    layer.renderRoundedPolygon(polygon, options)
}

private fun getFork(state: CharacterRenderState, distance: Distance, widthFactor: Factor): Polygon2d {
    val padding = state.config.head.hair.longPadding
    val width = FULL + padding * 2.0f
    val startY = HALF
    val center = state.aabb.getPoint(CENTER, FULL)
    val (left, right) = state.aabb.getMirroredPoints(widthFactor, FULL)
    val (innerLeft, innerRight) = state.aabb.getMirroredPoints(widthFactor * HALF, FULL)

    return Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, startY)
        .addMirroredPoints(state.aabb, width, startY)
        .addMirroredPoints(state.aabb, width, END)
        .addPoints(left.addHeight(distance), right.addHeight(distance))
        .addPoints(innerLeft.addHeight(distance), innerRight.addHeight(distance))
        .addLeftPoint(center)
        .build()
}


private fun getRectangle(state: CharacterRenderState, distance: Distance, widthFactor: Factor): Polygon2d {
    val padding = state.config.head.hair.longPadding
    val width = FULL + padding * 2.0f
    val startY = HALF
    val (left, right) = state.aabb.getMirroredPoints(width * widthFactor, FULL)

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
    val (left, right) = state.aabb.getMirroredPoints(Factor.fromPercentage(30), FULL)

    return Polygon2dBuilder()
        .addLeftPoint(state.aabb, CENTER, startY)
        .addMirroredPoints(state.aabb, width, startY)
        .addMirroredPoints(state.aabb, width, END)
        .addPoints(left.addHeight(distance), right.addHeight(distance))
        .build()
}