package at.orchaldir.gm.prototypes.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.OverwriteFeatureColor
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHorn
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHornType
import at.orchaldir.gm.core.model.character.appearance.horn.TwoHorns
import at.orchaldir.gm.core.model.race.appearance.scaleSimpleLength
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()

    listOf(60, 80, 100).forEach { length ->
        val horns = mutableListOf<Appearance>()

        SimpleHornType.entries.forEach { type ->
            horns.add(createTwoHorns(scaleSimpleLength(type, fromPercentage(length)), type))
        }

        appearances.add(horns)
    }

    renderCharacterTable(State(), "horns-simple.svg", CHARACTER_CONFIG, appearances)
}

private fun createTwoHorns(length: Factor, type: SimpleHornType) =
    createAppearance(TwoHorns(SimpleHorn(length, type, OverwriteFeatureColor(Color.Red))))


