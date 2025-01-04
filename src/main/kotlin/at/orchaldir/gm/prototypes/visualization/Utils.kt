package at.orchaldir.gm.prototypes.visualization

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import java.io.File

fun <T> renderTable(
    filename: String,
    size: Size2d,
    rows: List<List<T>>,
    render: (AABB, MultiLayerRenderer, T) -> Unit,
) {
    val maxColumns = rows.maxOf { it.size }
    val totalSize = Size2d(size.width * maxColumns, size.height * rows.size)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()

    rows.forEach { row ->
        var start = startOfRow.copy()

        row.forEach { element ->
            val aabb = AABB(start, size)

            render(aabb, builder, element)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File(filename).writeText(builder.finish().export())
}
