package at.orchaldir.gm.prototypes.visualization.book

import at.orchaldir.gm.core.model.item.book.BookFormat
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.book.BookRenderConfig
import at.orchaldir.gm.visualization.book.BookRenderState
import at.orchaldir.gm.visualization.book.book.visualizeBookFormat

fun <C, R> renderBookTable(
    filename: String,
    config: BookRenderConfig,
    size: Size2d,
    columns: List<Pair<String, C>>,
    rows: List<Pair<String, R>>,
    create: (C, R) -> BookFormat,
) {
    renderTable(filename, size, rows, columns, false) { aabb, renderer, _, column, row ->
        val format = create(column, row)
        val state = BookRenderState(aabb, config, renderer)

        visualizeBookFormat(state, format)
    }
}
