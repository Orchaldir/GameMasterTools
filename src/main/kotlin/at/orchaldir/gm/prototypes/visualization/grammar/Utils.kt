package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar

val LINE_OPTIONS = LineOptions(Color.Black.toRender(), fromMillimeters(5))

fun <C, R> renderGrammarTable(
    state: State,
    filename: String,
    renderSize: Size2d,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    create: (C, R) -> ShapeGrammar,
) {
    renderGrammarTable(
        state,
        filename,
        rows,
        columns,
    ) { column, row ->
        Pair(create(column, row), PaddedSize(renderSize))
    }
}

fun <C, R> renderGrammarTable(
    state: State,
    filename: String,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    process: (C, R) -> Pair<ShapeGrammar, PaddedSize>,
) {
    renderTable(
        filename,
        rows,
        columns,
        Size2d.fromMeters(0.1f),
        false,
        process,
    ) { renderAabb, renderer, _, grammar ->
        val renderState = GrammarRenderState(state, renderer, LINE_OPTIONS)

        visualizeShapeGrammar(renderState, grammar, renderAabb)
    }
}
