package at.orchaldir.gm.prototypes.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHorn
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHornType
import at.orchaldir.gm.core.model.character.appearance.horn.TwoHorns
import at.orchaldir.gm.core.model.race.appearance.scaleSimpleLength
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()

    listOf(Factor(0.6f), Factor(0.8f), Factor(1.0f)).forEach { length ->
        val horns = mutableListOf<Appearance>()

        SimpleHornType.entries.forEach { type ->
            horns.add(createTwoHorns(scaleSimpleLength(type, length), type))
        }

        appearances.add(horns)
    }

    renderCharacterTable("horns-simple.svg", CHARACTER_CONFIG, appearances)
}

private fun createTwoHorns(length: Factor, type: SimpleHornType) =
    createAppearance(TwoHorns(SimpleHorn(length, type, Color.Red)))


