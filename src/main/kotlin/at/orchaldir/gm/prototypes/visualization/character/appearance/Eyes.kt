package at.orchaldir.gm.prototypes.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.ExoticSkin
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.character.CHARACTER_CONFIG
import at.orchaldir.gm.prototypes.visualization.character.renderCharacterTable
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val appearances = mutableListOf<List<Appearance>>()

    PupilShape.entries.forEach { pupilShape ->
        EyeShape.entries.forEach { eyeShape ->
            appearances.add(createRow(NormalEye(eyeShape, pupilShape)))
        }
    }

    renderCharacterTable("eyes.svg", CHARACTER_CONFIG, appearances)
}

private fun createAppearance(eyes: Eyes) = HeadOnly(Head(eyes = eyes, skin = ExoticSkin()), fromMillimeters(200))

private fun createRow(eye: Eye) =
    listOf(OneEye(eye, Size.Small), OneEye(eye, Size.Medium), OneEye(eye, Size.Large), TwoEyes(eye))
        .map { createAppearance(it) }