package at.orchaldir.gm.visualization

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun renderBuilder(
    renderer: MultiLayerRenderer,
    builder: Polygon2dBuilder,
    options: RenderOptions,
    layer: Int,
) {
    val polygon = builder.build()

    renderer.getLayer(layer).renderPolygon(polygon, options)
}

fun renderRoundedBuilder(
    renderer: MultiLayerRenderer,
    builder: Polygon2dBuilder,
    options: RenderOptions,
    layer: Int,
) {
    val polygon = builder.build()

    renderer.getLayer(layer).renderRoundedPolygon(polygon, options)
}

fun renderPolygon(
    renderer: LayerRenderer,
    options: RenderOptions,
    corners: List<Point2d>,
) {
    val polygon = Polygon2d(corners)

    renderer.renderPolygon(polygon, options)
}

fun renderRoundedPolygon(
    renderer: LayerRenderer,
    options: RenderOptions,
    corners: List<Point2d>,
) {
    val polygon = Polygon2d(corners)

    renderer.renderRoundedPolygon(polygon, options)
}

fun renderMirroredPolygons(
    renderer: LayerRenderer,
    options: RenderOptions,
    aabb: AABB,
    corners: List<Point2d>,
) {
    val polygon = Polygon2d(corners)
    val mirror = aabb.mirrorVertically(polygon)

    renderer.renderPolygon(polygon, options)
    renderer.renderPolygon(mirror, options)
}