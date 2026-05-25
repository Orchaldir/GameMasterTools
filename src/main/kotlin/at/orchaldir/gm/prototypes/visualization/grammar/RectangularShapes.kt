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
        addNames(RectangularShape.entries),
        listOf(
            Pair("Horizontal", Size2d.fromMeters(0.1f, 0.05f)),
            Pair("Vertical", Size2d.fromMeters( 0.05f, 0.1f)),
            Pair("Square", Size2d.fromMeters( 0.05f)),
        ),
        ::createGrammar,
    )
}

private fun createGrammar(size: Size2d, shape: RectangularShape) = Pair(
    RectangularShapeGrammar(
        shape,
        MadeFromWood(color = Color.Gray),
    ),
    PaddedSize(size, Distance.fromMeters(0.01f)),
)