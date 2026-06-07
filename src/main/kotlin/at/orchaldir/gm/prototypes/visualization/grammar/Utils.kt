package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.grammar.Borders
import at.orchaldir.gm.visualization.grammar.GrammarRenderState
import at.orchaldir.gm.visualization.grammar.visualizeShapeGrammar

val GRAMMAR_SIZE = PaddedSize(Size2d.square(Distance.fromMeters(1)), Distance.fromMeters(0.2f))
val LINE_OPTIONS = LineOptions(Color.Black.toRender(), fromMillimeters(5))

fun <C, R> renderGrammarTable(
    state: State,
    filename: String,
    size: PaddedSize,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    create: (C, R) -> ShapeGrammar,
) = renderGrammarTableWithBorders(
    state,
    filename,
    rows,
    columns,
) { column, row ->
    Pair(Pair(create(column, row), Borders()), size)
}

fun <C, R> renderGrammarTable(
    state: State,
    filename: String,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    process: (C, R) -> Pair<ShapeGrammar, PaddedSize>,
)  = renderGrammarTableWithBorders(
    state,
    filename,
    rows,
    columns,
) { column, row ->
    val (grammar, size) = process(column, row)

    Pair(Pair(grammar, Borders()), size)
}

fun <C, R> renderGrammarTableWithBorders(
    state: State,
    filename: String,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    process: (C, R) -> Pair<Pair<ShapeGrammar, Borders>, PaddedSize>,
) = renderTable(
    filename,
    rows,
    columns,
    Size2d.fromMeters(0.1f),
    false,
    process,
) { renderAabb, renderer, _, (grammar, borders) ->
    val renderState = GrammarRenderState(state, renderer, LINE_OPTIONS)

    visualizeShapeGrammar(renderState, grammar, renderAabb, borders)
}
