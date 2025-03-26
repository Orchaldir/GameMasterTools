package at.orchaldir.gm.prototypes.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.NormalEars
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Orientation.Companion.fromDegree
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()
    val mouflon = CurvedHorn(fromDegree(180.0f))
    val waterBuffalo = CurvedHorn(fromDegree(-120.0f))
    val wave = SpiralHorn(4, fromPercentage(10))

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

    renderCharacterTable("horns-complex.svg", CHARACTER_CONFIG, appearances)
}

private fun createTwoHorns(position: HornPosition, orientation: Orientation, shape: HornShape) =
    createAppearance(TwoHorns(createHorn(position, orientation, shape)))

private fun createHorn(position: HornPosition, orientation: Orientation, shape: HornShape) = ComplexHorn(
    fromPercentage(100),
    fromPercentage(20),
    position,
    orientation,
    shape,
    Color.Red,
)

fun createAppearance(horns: Horns) =
    HeadOnly(
        Head(
            NormalEars(),
            TwoEyes(),
            horns = horns
        ),
        fromMillimeters(500),
    )