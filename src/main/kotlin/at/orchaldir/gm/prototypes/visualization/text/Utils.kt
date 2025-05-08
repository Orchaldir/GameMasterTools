package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.ResolvedTextData
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.text.visualizeTextFormat

fun renderTextFormatTable(
    filename: String,
    state: State,
    config: TextRenderConfig,
    texts: List<List<TextFormat>>,
) {
    val size = config.calculatePaddedSize(texts[0][0])

    renderTable(filename, size, texts) { aabb, renderer, format ->
        val state = TextRenderState(state, aabb, config, renderer)

        visualizeTextFormat(state, format)
    }
}

fun renderResolvedTextTable(
    filename: String,
    state: State,
    config: TextRenderConfig,
    texts: List<List<Pair<TextFormat, ResolvedTextData>>>,
) {
    val size = config.calculatePaddedSize(texts[0][0].first)

    renderTable(filename, size, texts) { aabb, renderer, (format, data) ->
        val state = TextRenderState(state, aabb, config, renderer, data)

        visualizeTextFormat(state, format)
    }
}

fun <C, R> renderTextFormatTable(
    filename: String,
    state: State,
    config: TextRenderConfig,
    size: Size2d,
    columns: List<Pair<String, C>>,
    rows: List<Pair<String, R>>,
    data: ResolvedTextData = ResolvedTextData(),
    create: (C, R) -> TextFormat,
) {
    renderTable(filename, size, rows, columns, false) { aabb, renderer, _, column, row ->
        val format = create(column, row)
        val state = TextRenderState(state, aabb, config, renderer, data)

        visualizeTextFormat(state, format)
    }
}
