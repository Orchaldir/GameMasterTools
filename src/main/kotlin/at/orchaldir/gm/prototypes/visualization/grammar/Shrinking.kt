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
        listOf(
            Pair("Min", MIN_SHRINK_FACTOR),
            Pair("Default", DEFAULT_SHRINK_FACTOR),
            Pair("Max", MAX_SHRINK_FACTOR),
        ),
        addNames(RectangularShape.entries),
        ::createGrammar,
    )
}

private fun createGrammar(shape: RectangularShape, factor: Factor) = Pair(
    BrickPatternGrammar(
        ShrinkGrammar(
            RectangularShapeGrammar(
                MadeFromStone(Color.Gray),
                shape,
            ),
            factor,
        ),
        SquareGrid(5),
        BrickPattern.Herringbone,
    ),
    PaddedSize(Size2d.square(Distance.fromMeters(1)), Distance.fromMeters(0.2f)),
)