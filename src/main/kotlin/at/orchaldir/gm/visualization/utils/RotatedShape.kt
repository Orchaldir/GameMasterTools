package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeRotatedShape(
    renderer: LayerRenderer,
    options: RenderOptions,
    aabb: AABB,
    shape: RotatedShape,
    side: Side,
) {
    val polygon = createRotatedShapePolygon(shape, aabb)
    val mirrored = if (side == Side.Right) { polygon }
    else { aabb.mirrorVertically(polygon) }

    if (shape.rounded) {
        renderer.renderRoundedPolygon(mirrored, options)
    } else {
        renderer.renderPolygon(mirrored, options)
    }
}

private fun createRotatedShapePolygon(
    shape: RotatedShape,
    aabb: AABB,
): Polygon2d {
    val builder = Polygon2dBuilder()
        .addLeftPoint(aabb, START, START, true)

    shape.profile.forEach { (y, x) ->
        val horizontal = Factor.fromPercentage(x)
        val vertical = Factor.fromPercentage(y)

        builder.addRightPoint(aabb, horizontal, vertical)
    }

    return builder
        .addLeftPoint(aabb, START, END, true)
        .build()
}
