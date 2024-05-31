package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.calculateSize
import at.orchaldir.gm.visualization.character.visualizeAppearance
import java.io.File

fun main() {
    val config = RENDER_CONFIG
    val appearances = mutableListOf<List<Appearance>>()
    PupilShape.entries.forEach { pupilShape ->
        EyeShape.entries.forEach { eyeShape ->
            appearances.add(createRow(Eye(eyeShape, pupilShape)))
        }
    }
    val size = calculateSize(config, appearances[0][0])
    val maxColumns = appearances.maxOf { it.size }
    val totalSize = Size2d(size.width * maxColumns, size.height * appearances.size)
    val builder = SvgBuilder.create(totalSize)
    val columnStep = Point2d(size.width, 0.0f)
    val rowStep = Point2d(0.0f, size.height)
    var startOfRow = Point2d()

    appearances.forEach { row ->
        var start = startOfRow.copy()

        row.forEach { appearance ->
            val aabb = AABB(start, size)

            visualizeAppearance(builder, config, aabb, appearance)

            start += columnStep
        }

        startOfRow += rowStep
    }

    File("eyes.svg").writeText(builder.finish().export())
}

private fun createAppearance(eyes: Eyes) = HeadOnly(Head(eyes = eyes, skin = ExoticSkin()), Distance(0.2f))

private fun createRow(eye: Eye) =
    listOf(OneEye(eye, Size.Small), OneEye(eye, Size.Medium), OneEye(eye, Size.Large), TwoEyes(eye))
        .map { createAppearance(it) }