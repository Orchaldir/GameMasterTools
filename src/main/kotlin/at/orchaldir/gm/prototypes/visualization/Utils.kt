package at.orchaldir.gm.prototypes.visualization

import at.orchaldir.gm.core.model.util.HorizontalAlignment.End
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.appearance.TEXT_LAYER
import java.io.File

fun <T> renderTable(
    filename: String,
    renderSize: Size2d,
    rows: List<List<T>>,
    render: (AABB, MultiLayerRenderer, T) -> Unit,
) {
    val maxColumns = rows.maxOf { it.size }
    val totalSize = Size2d(renderSize.width * maxColumns, renderSize.height * rows.size)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d.xAxis(renderSize.width)
    val rowStep = Point2d.yAxis(renderSize.height)
    var startOfRow = Point2d()

    rows.forEach { row ->
        var start = startOfRow.copy()

        row.forEach { element ->
            val aabb = AABB(start, renderSize)

            render(aabb, builder, element)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File(filename).writeText(builder.finish().export())
}

fun <T> renderTableWithNames(
    filename: String,
    renderSize: Size2d,
    rows: List<List<Pair<String, T>>>,
    render: (AABB, MultiLayerRenderer, T) -> Unit,
) {
    val textSize = renderSize.width / 10.0f
    val textOptions = RenderStringOptions(Color.Black.toRender(), textSize)

    renderTable(filename, renderSize, rows) { aabb, renderer, pair ->
        render(aabb, renderer, pair.second)

        renderer.getLayer().renderString(pair.first, aabb.getCenter(), ZERO_ORIENTATION, textOptions)
    }
}

fun <C, R> renderTable(
    filename: String,
    renderSize: Size2d,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    backToo: Boolean,
    render: (AABB, MultiLayerRenderer, Boolean, C, R) -> Unit,
) {
    val rowSize = if (backToo) {
        2
    } else {
        1
    }
    val totalSize = Size2d(renderSize.width * columns.size, renderSize.height * rows.size * rowSize)
    val builder = SvgBuilder(totalSize)
    val columnStep = Point2d.xAxis(renderSize.width)
    val rowStep = Point2d.yAxis(renderSize.height)
    var startOfRow = Point2d()
    val textSize = renderSize.width / 10.0f
    val halfTextSize = textSize / 2.0f
    val textOptions = RenderStringOptions(Color.Black.toRender(), textSize)
    val verticalOptions = RenderStringOptions(Color.Black.toRender(), textSize, horizontalAlignment = End)
    val columnTextOffset = Point2d(renderSize.width, halfTextSize)
    val columnOrientation = Orientation.zero()
    val rowOrientation = Orientation.fromDegrees(270)
    val layer = builder.getLayer(TEXT_LAYER)

    rows.forEach { (rowName, row) ->
        var start = startOfRow.copy()

        columns.forEach { (columnName, column) ->
            val aabb = AABB(start, renderSize)

            render(aabb, builder, true, column, row)

            if (backToo) {
                val startBack = start + rowStep
                val aabbBack = AABB(startBack, renderSize)

                render(aabbBack, builder, false, column, row)
            }

            val textCenter = start + columnTextOffset
            layer.renderString(columnName, textCenter, columnOrientation, verticalOptions)

            start += columnStep
        }

        val textCenter = Point2d(halfTextSize, start.y + renderSize.height / 2.0f)
        layer.renderString(rowName, textCenter, rowOrientation, textOptions)

        if (backToo) {
            layer.renderString("Back", textCenter + rowStep, rowOrientation, textOptions)
        }

        startOfRow += rowStep * rowSize
    }

    File(filename).writeText(builder.finish().export())
}

fun <T> addNames(value: T) = addNames(setOf(value))

fun <T> addNames(values: Collection<T>) = values.map {
    Pair(it.toString(), it)
}
