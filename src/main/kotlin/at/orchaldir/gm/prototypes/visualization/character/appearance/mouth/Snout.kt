package at.orchaldir.gm.prototypes.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.ExoticSkin
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.OneEye
import at.orchaldir.gm.core.model.character.appearance.eye.TwoEyes
import at.orchaldir.gm.core.model.character.appearance.mouth.Beak
import at.orchaldir.gm.core.model.character.appearance.mouth.Snout
import at.orchaldir.gm.core.model.character.appearance.mouth.SnoutShape
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val appearances = mutableListOf<List<Appearance>>()

    SnoutShape.entries.forEach { shape ->
        appearances.add(createRow(shape))
    }

    renderCharacterTable("snouts.svg", CHARACTER_CONFIG, appearances)
}


private fun createRow(shape: SnoutShape) =
    listOf(OneEye(size = Size.Small), OneEye(size = Size.Medium), OneEye(size = Size.Large), TwoEyes())
        .map { createAppearance(shape, it) }


private fun createAppearance(shape: SnoutShape, eyes: Eyes) =
    HeadOnly(
        Head(
            eyes = eyes,
            mouth = Snout(shape),
            skin = ExoticSkin(),
        ),
        fromMillimeters(1000),
    )