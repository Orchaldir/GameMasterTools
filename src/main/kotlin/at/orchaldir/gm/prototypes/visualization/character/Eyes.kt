package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.RENDER_CONFIG
import at.orchaldir.gm.utils.math.Distance

fun main() {
    val appearances = mutableListOf<List<Appearance>>()
    PupilShape.entries.forEach { pupilShape ->
        EyeShape.entries.forEach { eyeShape ->
            appearances.add(createRow(Eye(eyeShape, pupilShape)))
        }
    }

    renderTable("eyes.svg", RENDER_CONFIG, appearances)
}

private fun createAppearance(eyes: Eyes) = HeadOnly(Head(eyes = eyes, skin = ExoticSkin()), Distance(0.2f))

private fun createRow(eye: Eye) =
    listOf(OneEye(eye, Size.Small), OneEye(eye, Size.Medium), OneEye(eye, Size.Large), TwoEyes(eye))
        .map { createAppearance(it) }