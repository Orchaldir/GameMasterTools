package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.visualization.Grammar
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d

fun <C, R> renderGrammarTable(
    state: State,
    filename: String,
    renderSize: Size2d,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    create: (C, R) -> Grammar,
) {
    renderTable(
        filename,
        renderSize,
        rows,
        columns,
        false,
    ) { renderAabb, renderer, renderFront, column, row ->

    }
}
