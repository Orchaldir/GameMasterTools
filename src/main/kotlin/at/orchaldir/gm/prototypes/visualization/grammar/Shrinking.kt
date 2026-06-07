package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.appearance.PaddedSize

fun main() {
    renderGrammarTable(
        State(),
        "grammar-shrinking.svg",
        GRAMMAR_SIZE,
        listOf(
            Pair("Min", MIN_SHRINK_FACTOR),
            Pair("Default", DEFAULT_SHRINK_FACTOR),
            Pair("Max", MAX_SHRINK_FACTOR),
        ),
        addNames(RectangularShape.entries),
        ::createGrammar,
    )
}

private fun createGrammar(shape: RectangularShape, factor: Factor) = BrickPatternGrammar(
    ShrinkGrammar(
        RectangularShapeGrammar(
            MadeFromStone(Color.Gray),
            shape,
        ),
        factor,
    ),
    SquareGrid(5),
    BrickPattern.Herringbone,
)