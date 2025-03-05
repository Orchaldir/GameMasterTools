package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Distance

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf<List<Appearance>>()

    Size.entries.forEach { size ->
        appearances.add(HornSetShape.entries.map { createAppearance(TwoHorns(createHorn(it, size))) })
    }
    appearances.add(
        listOf(
            createAppearance(DifferentHorns(createHorn(HornSetShape.Bull), createHorn(HornSetShape.Goat))),
            createAppearance(DifferentHorns(createHorn(HornSetShape.Goat), createHorn(HornSetShape.Mouflon))),
            createAppearance(DifferentHorns(createHorn(HornSetShape.Mouflon), createHorn(HornSetShape.Sheep))),
            createAppearance(DifferentHorns(createHorn(HornSetShape.Sheep), createHorn(HornSetShape.Bull))),
        )
    )


    renderCharacterTable("horns.svg", CHARACTER_CONFIG, appearances)
}

private fun createHorn(shape: HornSetShape, size: Size = Size.Medium) = HornSet(shape, size)

private fun createAppearance(horns: Horns) =
    HeadOnly(
        Head(horns = horns),
        Distance(200),
    )