package at.orchaldir.gm.prototypes.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.ExoticSkin
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.character.appearance.mouth.Beak
import at.orchaldir.gm.core.model.character.appearance.mouth.BeakShape
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance

fun main() {
    val appearances = mutableListOf<List<Appearance>>()

    BeakShape.entries.forEach { shape ->
        appearances.add(createRow(shape))
    }

    renderCharacterTable("beaks.svg", CHARACTER_CONFIG, appearances)
}


private fun createRow(beakShape: BeakShape) =
    listOf(OneEye(size = Size.Small), OneEye(size = Size.Medium), OneEye(size = Size.Large), TwoEyes())
        .map { createAppearance(beakShape, it) }


private fun createAppearance(beakShape: BeakShape, eyes: Eyes) =
    HeadOnly(
        Head(
            eyes = eyes,
            mouth = Beak(beakShape),
            skin = ExoticSkin(),
        ),
        Distance(1000),
    )