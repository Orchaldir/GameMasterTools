package at.orchaldir.gm.prototypes.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
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
        HeadConfig(EyesConfig(SizeConfig(0.2f, 0.3f, 0.4f), SizeConfig(0.3f, 0.45f, 0.5f)), 0.4f)
    )
    val eyes =
        listOf(NoEyes, OneEye(size = Size.Small), OneEye(size = Size.Medium), OneEye(size = Size.Large), TwoEyes())
    val appearances = eyes.map { create(it) }
    val size = calculateSize(config, appearances[0])
    val totalSize = Size2d(size.width * appearances.size, size.height)
    val builder = SvgBuilder.create(totalSize)
    var aabb = AABB(size)
    val step = Point2d(size.width, 0.0f)

    appearances.forEach { appearance ->
        visualizeAppearance(builder, config, aabb, appearance)

        aabb += step
    }

    File("eyes.svg").writeText(builder.finish().export())
}

private fun create(eyes: Eyes) = HeadOnly(Head(eyes = eyes), ExoticSkin(), Distance(0.2f))