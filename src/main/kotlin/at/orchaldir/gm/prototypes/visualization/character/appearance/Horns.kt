package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()
    val mouflon = ConstantCurvature(
        Orientation.fromDegree(0.0f),
        Orientation.fromDegree(270.0f),
    )

    HornPosition.entries.forEach { position ->
        val horns = mutableListOf<Appearance>()

        horns.add(createTwoHorns(position, StraightHorn(Orientation.fromDegree(0.0f))))
        horns.add(createTwoHorns(position, mouflon))

        appearances.add(horns)
    }

    renderCharacterTable("horns.svg", CHARACTER_CONFIG, appearances)
}

private fun createTwoHorns(position: HornPosition, curve: HornCurve) =
    createAppearance(TwoHorns(createHorn(position, curve)))

private fun createHorn(position: HornPosition, curve: HornCurve) = CurvedHorn(
    Factor(1.0f),
    Factor(0.2f),
    position,
    curve
)

private fun createAppearance(horns: Horns) =
    HeadOnly(
        Head(horns = horns),
        Distance(200),
    )