package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.prototypes.visualization.addNames

fun main() {
    renderGrammarTable(
        State(),
        "grammar-single-brick-patterns.svg",
        GRAMMAR_SIZE,
        addNames(listOf(2, 3, 4)),
        addNames(BrickPattern.Pinwheel),
        ::createGrammar,
    )
}

private fun createGrammar(pattern: BrickPattern, length: Int) = BrickPatternGrammar(
    RectangularShapeGrammar(Color.Gray),
    SquareGrid(20),
    pattern,
    length,
)