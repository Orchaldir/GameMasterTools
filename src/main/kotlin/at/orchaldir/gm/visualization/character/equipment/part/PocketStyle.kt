package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
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

fun visualizeTopPockets(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    style: PocketStyle,
    layer: Int,
) {
    if (style == PocketStyle.None) {
        return
    }

    val y = fromPercentage(55)
    val (left, right) = state.fullAABB.getMirroredPoints(fromPercentage(25), y)
    val width = state.fullAABB.convertWidth(fromPercentage(10))

    visualizePocket(state, options, style, left, width, layer)
    visualizePocket(state, options, style, right, width, layer)
}

fun visualizePocket(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    style: PocketStyle,
    position: Point2d,
    width: Distance,
    layer: Int,
) {
    val half = width / 2.0f
    val start = position.minusWidth(half)
    val renderer = state.renderer.getLayer(layer)

    when (style) {
        PocketStyle.Flaps -> {
            val aabb = AABB(start, Size2d(width, half))

            renderer.renderRectangle(aabb, options)
        }

        PocketStyle.Jetted -> {
            val line = listOf(
                position.minusWidth(half),
                position.addWidth(half),
            )

            renderer.renderLine(line, state.config.line)
        }

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
