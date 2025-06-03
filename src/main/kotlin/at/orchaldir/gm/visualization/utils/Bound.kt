package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder

fun visualizeBoundRows(
    renderer: LayerRenderer,
    options: FillAndBorder,
    aabb: AABB,
    rowHeight: Distance,
    rows: Int,
) {
    var start = aabb.getPoint(START, START)
    val size = Size2d(aabb.size.width, rowHeight)

    repeat(rows) {
        val aabb = AABB(start, size)
        val polygon = Polygon2dBuilder()
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, HALF)
            .addMirroredPoints(aabb, FULL, END)
            .build()

        renderer.renderRoundedPolygon(polygon, options)

        start = start.addHeight(rowHeight)
    }
}