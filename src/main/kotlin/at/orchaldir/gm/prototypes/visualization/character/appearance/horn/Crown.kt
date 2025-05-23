package at.orchaldir.gm.prototypes.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.OverwriteFeatureColor
import at.orchaldir.gm.core.model.character.appearance.horn.CrownOfHorns
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()

    addRow(appearances, true)
    addRow(appearances, false)

    renderCharacterTable(State(), "horns-crown.svg", CHARACTER_CONFIG, appearances)
}

private fun addRow(appearances: MutableList<List<Appearance>>, hasSideHorns: Boolean) {
    appearances.add(
        listOf(
            createCrown(1, 2, hasSideHorns),
            createCrown(2, 1, hasSideHorns),
            createCrown(3, 2, hasSideHorns),
        )
    )
}

private fun createCrown(front: Int, back: Int, hasSideHorns: Boolean) = createAppearance(
    CrownOfHorns(
        front,
        back,
        hasSideHorns,
        fromPercentage(30),
        fromPercentage(15),
        OverwriteFeatureColor(Color.Blue),
    )
)
