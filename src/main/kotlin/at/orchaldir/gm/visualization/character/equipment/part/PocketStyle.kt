package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.PocketStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

fun visualizeTopPockets(
    state: CharacterRenderState,
    options: RenderOptions,
    style: PocketStyle,
) {
    if (style == PocketStyle.None) {
        return
    }

    val y = fromPercentage(55)
    val (left, right) = state.aabb.getMirroredPoints(fromPercentage(25), y)
    val width = state.aabb.convertWidth(fromPercentage(10))

    visualizePocket(state, options, style, left, width)
    visualizePocket(state, options, style, right, width)
}

fun visualizePocket(
    state: CharacterRenderState,
    options: RenderOptions,
    style: PocketStyle,
    position: Point2d,
    width: Distance,
) {
    val start = position.minusWidth(width / 2.0f)
    val renderer = state.renderer.getLayer(ABOVE_EQUIPMENT_LAYER)

    when (style) {
        PocketStyle.Flaps -> {
            val aabb = AABB(start, Size2d(width, width / 2.0f))

            renderer.renderRectangle(aabb, options)
        }
        PocketStyle.Jetted -> doNothing()
        PocketStyle.None -> doNothing()
        PocketStyle.Patch -> {
            val (topLeft, topRight, bottomRight, bottomLeft) = AABB(start, Size2d.square(width)).getCorners()
            val polygon = Polygon2dBuilder()
                .addPoints(topLeft, topRight, true)
                .addPoints(bottomLeft, bottomRight)
                .build()

            renderer.renderRoundedPolygon(polygon, options)
        }
    }
}
