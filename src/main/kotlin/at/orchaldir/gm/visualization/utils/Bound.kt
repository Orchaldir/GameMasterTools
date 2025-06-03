package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeBoundRows(
    renderer: LayerRenderer,
    options: RenderOptions,
    aabb: AABB,
    rows: Int,
) = visualizeBoundRows(
    renderer,
    options,
    aabb,
    aabb.size.height / rows,
    rows,
)

fun visualizeBoundRows(
    renderer: LayerRenderer,
    options: RenderOptions,
    aabb: AABB,
    rowHeight: Distance,
    rows: Int,
) {
    var start = aabb.getPoint(START, START)
    val size = Size2d(aabb.size.width, rowHeight)

    repeat(rows) {
        val aabb = AABB(start, size)
        val polygon = Polygon2dBuilder()
            .addLeftPoint(aabb, CENTER, START)
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, HALF)
            .addMirroredPoints(aabb, FULL, END)
            .addLeftPoint(aabb, CENTER, END)
            .build()

        renderer.renderRoundedPolygon(polygon, options)

        start = start.addHeight(rowHeight)
    }
}