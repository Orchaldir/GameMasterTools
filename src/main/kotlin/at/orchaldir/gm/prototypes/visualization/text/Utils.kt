package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.text.visualizeTextFormat

fun renderTextTable(
    filename: String,
    config: TextRenderConfig,
    texts: List<List<TextFormat>>,
) {
    val size = config.calculatePaddedSize(texts[0][0])

    renderTable(filename, size, texts) { aabb, renderer, format ->
        val state = TextRenderState(aabb, config, renderer)

        visualizeTextFormat(state, format)
    }
}

fun <C, R> renderTextTable(
    filename: String,
    config: TextRenderConfig,
    size: Size2d,
    columns: List<Pair<String, C>>,
    rows: List<Pair<String, R>>,
    create: (C, R) -> TextFormat,
) {
    renderTable(filename, size, rows, columns, false) { aabb, renderer, _, column, row ->
        val format = create(column, row)
        val state = TextRenderState(aabb, config, renderer)

        visualizeTextFormat(state, format)
    }
}
