package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.grammar.Borders

fun main() {
    renderGrammarTableWithBorders(
        State(),
        "grammar-borders.svg",
        addNames(listOf(true, false)),
        addNames(BrickPattern.entries),
        ::createGrammar,
    )
}

private fun createGrammar(pattern: BrickPattern, isBorder: Boolean) = Pair(
    Pair(
        BrickPatternGrammar(
            RectangularShapeGrammar(
                MadeFromWood(color = Color.Gray),
            ),
            SquareGrid(10),
            pattern,
            3,
        ),
        Borders(isBorder),
    ),
    GRAMMAR_SIZE,
)