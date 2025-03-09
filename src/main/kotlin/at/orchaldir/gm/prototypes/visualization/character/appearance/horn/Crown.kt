package at.orchaldir.gm.prototypes.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.horn.CrownOfHorns
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()

    appearances.add(
        listOf(
            createCrown(1, 2, true),
            createCrown(1, 2, false),
            createCrown(2, 1, true),
            createCrown(2, 1, false),
            createCrown(3, 2, true),
            createCrown(3, 2, false),
        )
    )

    renderCharacterTable("horns-crown.svg", CHARACTER_CONFIG, appearances)
}

private fun createCrown(front: Int, back: Int, hasSideHorns: Boolean) = createAppearance(
    CrownOfHorns(
        front,
        back,
        hasSideHorns,
        Factor(0.3f),
        Factor(0.15f),
        Color.Blue,
    )
)
