package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.EyesConfig
import at.orchaldir.gm.visualization.character.HeadConfig
import at.orchaldir.gm.visualization.character.calculateSize
import at.orchaldir.gm.visualization.character.visualizeAppearance
import java.io.File

fun main() {
    val config = RenderConfig(
        Distance(0.2f), LineOptions(Color.Black.toRender(), Distance(0.005f)),
        HeadConfig(
            EyesConfig(
                SizeConfig(0.2f, 0.3f, 0.4f),
                SizeConfig(0.3f, 0.45f, 0.5f),
                Factor(0.7f),
                Factor(0.75f)
            ),
            Factor(0.4f)
        )
    )
    val appearances = EyeShape.entries.map { createRow(Eye(eyeShape = it)) }
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

private fun createAppearance(eyes: Eyes) = HeadOnly(Head(eyes = eyes), ExoticSkin(), Distance(0.2f))

private fun createRow(eye: Eye) =
    listOf(NoEyes, OneEye(eye, Size.Small), OneEye(eye, Size.Medium), OneEye(eye, Size.Large), TwoEyes(eye))
        .map { createAppearance(it) }