package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.visualization.Grammar
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.character.appearance.PaddedSize

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

fun <C, R> renderGrammarTable(
    state: State,
    filename: String,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    process: (C, R) -> Pair<Grammar, PaddedSize>,
) {
    renderTable(
        filename,
        rows,
        columns,
        Size2d.fromMeters(0.1f),
        false,
        process,
    ) { renderAabb, renderer, renderFront, grammar ->

    }
}
