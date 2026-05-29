package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SingleBrickGrammar
import at.orchaldir.gm.core.model.visualization.SingleBrickPattern
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.shape.RectangularShape
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    renderGrammarTable(
        State(),
        "grammar-single-brick-patterns.svg",
        Size2d.square(Distance.fromMeters(1)),
        addNames(listOf(
            RectangularShape.Rectangle,
            RectangularShape.RoundedRectangle,
        )),
        addNames(SingleBrickPattern.entries),
        ::createGrammar,
    )
}

private fun createGrammar(pattern: SingleBrickPattern, shape: RectangularShape) = SingleBrickGrammar(
    RectangularShapeGrammar(
        shape,
        MadeFromWood(color = Color.Gray),
    ),
    SquareGrid(20),
    pattern,
)