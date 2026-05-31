package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.shape.RectangularShape
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.appearance.PaddedSize


fun main() {
    renderGrammarTable(
        State(),
        "grammar-rectangular-shapes.svg",
        listOf(
            Pair("Horizontal", Size2d.fromMeters(1.0f, 0.5f)),
            Pair("Vertical", Size2d.fromMeters( 0.5f, 1.0f)),
            Pair("Square", Size2d.fromMeters( 0.5f)),
        ),
        addNames(RectangularShape.entries),
        ::createGrammar,
    )
}

private fun createGrammar(shape: RectangularShape, size: Size2d) = Pair(
    RectangularShapeGrammar(
        MadeFromWood(color = Color.Gray),
        shape,
    ),
    PaddedSize(size, Distance.fromMeters(0.2f)),
)