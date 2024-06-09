package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.renderPolygon

data class HairConfig(
    private val diameter: SizeConfig,
    private val distanceBetweenEyes: SizeConfig,
    private val almondHeight: Factor,
    private val ellipseHeight: Factor,
    val pupilFactor: Factor,
    val slitFactor: Factor,
) {

    fun getEyeSize(aabb: AABB, shape: EyeShape, size: Size = Size.Small): Size2d {
        val diameter = aabb.size.height * diameter.convert(size)

        return when (shape) {
            EyeShape.Almond -> Size2d(diameter, diameter * almondHeight.value)
            EyeShape.Circle -> square(diameter)
            EyeShape.Ellipse -> Size2d(diameter, diameter * ellipseHeight.value)
        }
    }

    fun getDistanceBetweenEyes(size: Size = Size.Medium): Factor {
        return Factor(distanceBetweenEyes.convert(size))
    }
}

fun visualizeHair(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is ShortHair -> visualizeShortHair(renderer, config, aabb, head.hair)
    }
}

fun visualizeShortHair(renderer: Renderer, config: RenderConfig, aabb: AABB, shortHair: ShortHair) {
    val options = FillAndBorder(shortHair.color.toRender(), config.line)

    when (shortHair.style) {
        ShortHairStyle.Afro -> doNothing()
        ShortHairStyle.BuzzCut -> {
            val (bottomLeft, bottomRight) = aabb.getMirroredPoints(Factor(1.0f), config.head.hairlineY)
            val (topLeft, topRight) = aabb.getMirroredPoints(Factor(1.0f), Factor(0.0f))

            renderPolygon(renderer, options, listOf(bottomLeft, bottomRight, topRight, topLeft))
        }

        ShortHairStyle.Curly -> doNothing()
        ShortHairStyle.FlatTop -> doNothing()
        ShortHairStyle.MiddlePart -> doNothing()
        ShortHairStyle.LeftSidePart -> doNothing()
        ShortHairStyle.RightSidePart -> doNothing()
        ShortHairStyle.Spiked -> doNothing()
    }
}
