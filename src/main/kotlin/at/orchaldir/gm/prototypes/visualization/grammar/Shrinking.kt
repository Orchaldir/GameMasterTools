package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Rock
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.ColorLookup
import at.orchaldir.gm.core.model.util.render.FixedColor
import at.orchaldir.gm.core.model.util.render.LookupMaterial
import at.orchaldir.gm.core.model.util.render.RandomColor
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.DEFAULT_SHRINK_FACTOR
import at.orchaldir.gm.core.model.visualization.MAX_SHRINK_FACTOR
import at.orchaldir.gm.core.model.visualization.MIN_SHRINK_FACTOR
import at.orchaldir.gm.core.model.visualization.RectangularShape
import at.orchaldir.gm.core.model.visualization.ShrinkGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage
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