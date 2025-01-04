package at.orchaldir.gm.prototypes.visualization.book

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.item.book.BookFormat
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.book.BookRenderConfig
import at.orchaldir.gm.visualization.book.BookRenderState
import at.orchaldir.gm.visualization.book.book.visualizeBookFormat
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.calculateSize
import at.orchaldir.gm.visualization.character.appearance.visualizeAppearance

fun <C, R> renderBookTable(
    filename: String,
    config: BookRenderConfig,
    size: Size2d,
    columns: List<Pair<String, C>>,
    rows: List<Pair<String, R>>,
    backToo: Boolean = false,
    create: (C, R) -> BookFormat,
) {
    renderTable(filename, size, rows, columns, backToo) { aabb, renderer, renderFront, column, row ->
        val format = create(column, row)
        val state = BookRenderState(aabb, config, renderer, renderFront)

        visualizeBookFormat(state, format)
    }
}
