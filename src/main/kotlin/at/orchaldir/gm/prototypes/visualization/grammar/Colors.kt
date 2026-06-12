package at.orchaldir.gm.prototypes.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.economy.material.Rock
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.util.render.*
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.SquareGrid
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.Storage

fun main() {
    val rock = Rock(
        OneOf(
            mapOf(
                Color.Gray to Rarity.Common,
                Color.LightGray to Rarity.Rare,
                Color.DimGray to Rarity.VeryRare,
            )
        )
    )
    val randomColors = RandomColor(
        OneOf(
            mapOf(
                Color.Blue to Rarity.Common,
                Color.Green to Rarity.Common,
            )
        )
    )
    val material = Material(MaterialId(0), properties = MaterialProperties(rock))
    val state = State(Storage(material))

    renderGrammarTable(
        state,
        "grammar-colors.svg",
        GRAMMAR_SIZE,
        listOf(
            Pair("Fixed", FixedColor(Color.Gray)),
            Pair("Material", LookupMaterial),
            Pair("Lookup", randomColors),
        ),
        addNames(BrickPattern.entries),
        ::createGrammar,
    )
}

private fun createGrammar(pattern: BrickPattern, lookup: ColorLookup) = BrickPatternGrammar(
    RectangularShapeGrammar(
        MadeFromStone(color = lookup),
    ),
    SquareGrid(20),
    pattern,
)