package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Orientation.Companion.fromDegree

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()
    val mouflon = ConstantCurvature(fromDegree(180.0f))
    val waterBuffalo = ConstantCurvature(fromDegree(-120.0f))
    val wave = WaveCurve(4, Factor(0.1f))

    HornPosition.entries.forEach { position ->
        val horns = mutableListOf<Appearance>()

        horns.add(createTwoHorns(position, fromDegree(0.0f), StraightHorn))
        horns.add(createTwoHorns(position, fromDegree(10.0f), StraightHorn))
        horns.add(createTwoHorns(position, fromDegree(-10.0f), StraightHorn))
        horns.add(createTwoHorns(position, fromDegree(-40.0f), mouflon))
        horns.add(createTwoHorns(position, fromDegree(0.0f), waterBuffalo))
        horns.add(createTwoHorns(position, fromDegree(0.0f), wave))

        appearances.add(horns)
    }
    appearances.add(
        listOf(
            createCrown(2, 1),
            createCrown(3, 2),
        )
    )

    renderCharacterTable("horns.svg", CHARACTER_CONFIG, appearances)
}

private fun createCrown(front: Int, back: Int) = createAppearance(
    CrownOfHorns(
        front,
        back,
        Factor(0.3f),
        Factor(0.15f),
        Color.Blue,
    )
)

private fun createTwoHorns(position: HornPosition, orientation: Orientation, curve: HornCurve) =
    createAppearance(TwoHorns(createHorn(position, orientation, curve)))

private fun createHorn(position: HornPosition, orientation: Orientation, curve: HornCurve) = CurvedHorn(
    Factor(1.0f),
    Factor(0.2f),
    position,
    orientation,
    curve,
    Color.Red,
)

private fun createAppearance(horns: Horns) =
    HeadOnly(
        Head(
            NormalEars(),
            TwoEyes(),
            horns = horns
        ),
        Distance(200),
    )