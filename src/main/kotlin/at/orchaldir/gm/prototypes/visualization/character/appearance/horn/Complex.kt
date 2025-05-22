package at.orchaldir.gm.prototypes.visualization.character.appearance.horn

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.horn.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees

fun main() {
    val appearances: MutableList<List<Appearance>> = mutableListOf()
    val mouflon = CurvedHorn(fromDegrees(180))
    val waterBuffalo = CurvedHorn(fromDegrees(-120))
    val wave = SpiralHorn(4, fromPercentage(10))

    HornPosition.entries.forEach { position ->
        val horns = mutableListOf<Appearance>()

        horns.add(createTwoHorns(position, fromDegrees(0), StraightHorn))
        horns.add(createTwoHorns(position, fromDegrees(10), StraightHorn))
        horns.add(createTwoHorns(position, fromDegrees(-10), StraightHorn))
        horns.add(createTwoHorns(position, fromDegrees(-40), mouflon))
        horns.add(createTwoHorns(position, fromDegrees(0), waterBuffalo))
        horns.add(createTwoHorns(position, fromDegrees(0), wave))

        appearances.add(horns)
    }

    renderCharacterTable(State(), "horns-complex.svg", CHARACTER_CONFIG, appearances)
}

private fun createTwoHorns(position: HornPosition, orientation: Orientation, shape: HornShape) =
    createAppearance(TwoHorns(createHorn(position, orientation, shape)))

private fun createHorn(position: HornPosition, orientation: Orientation, shape: HornShape) = ComplexHorn(
    fromPercentage(100),
    fromPercentage(20),
    position,
    orientation,
    shape,
    OverwriteFeatureColor(Color.Red),
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