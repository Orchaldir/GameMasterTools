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
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.appearance.PaddedSize

fun main() {
    val rock = Rock(OneOf(mapOf(
        Color.Gray to Rarity.Common,
        Color.LightGray to Rarity.Rare,
        Color.DimGray to Rarity.VeryRare,
    )))
    val randomColors = RandomColor(OneOf(mapOf(
        Color.Blue to Rarity.Common,
        Color.Green to Rarity.Common,
    )))
    val material = Material(MaterialId(0), properties = MaterialProperties(rock))
    val state = State(Storage(material))

    renderGrammarTable(
        state,
        "grammar-colors.svg",
        listOf(
            Pair("Fixed", FixedColor(Color.Gray)),
            Pair("Material", LookupMaterial),
            Pair("Lookup", randomColors),
        ),
        addNames(BrickPattern.entries),
        ::createGrammar,
    )
}

private fun createGrammar(pattern: BrickPattern, lookup: ColorLookup) = Pair(
    BrickPatternGrammar(
        RectangularShapeGrammar(
            MadeFromStone(color = lookup),
        ),
        SquareGrid(20),
        pattern,
    ),
    PaddedSize(Size2d.square(Distance.fromMeters(1)), Distance.fromMeters(0.2f)),
)