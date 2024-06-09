package at.orchaldir.gm.visualization

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.Renderer

fun renderPolygon(
    renderer: Renderer,
    options: RenderOptions,
    corners: List<Point2d>,
) {
    val polygon = Polygon2d(corners)

    renderer.renderPolygon(polygon, options)
}

fun renderMirroredPolygons(
    renderer: Renderer,
    options: RenderOptions,
    aabb: AABB,
    corners: List<Point2d>,
) {
    val polygon = Polygon2d(corners)
    val mirror = aabb.mirror(polygon)

    renderer.renderPolygon(polygon, options)
    renderer.renderPolygon(mirror, options)
}