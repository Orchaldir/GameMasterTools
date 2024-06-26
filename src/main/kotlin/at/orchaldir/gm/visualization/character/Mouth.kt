package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.beard.visualizeBeard

data class MouthConfig(
    private val simpleWidth: SizeConfig,
    val simpleHeight: Factor,
    val femaleHeight: Factor,
) {
    fun getSimpleWidth(size: Size) = Factor(simpleWidth.convert(size))

    fun getWidth(mouth: Mouth): Factor {
        val width = when (mouth) {
            is FemaleMouth -> mouth.width
            NoMouth -> Size.Medium
            is NormalMouth -> mouth.width
        }

        return getSimpleWidth(width)
    }

    fun getHeight(mouth: Mouth) = when (mouth) {
        is FemaleMouth -> femaleHeight
        NoMouth -> Factor(0.0f)
        is NormalMouth -> simpleHeight
    }
}

fun visualizeMouth(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.mouth) {
        NoMouth -> doNothing()
        is NormalMouth -> {
            val center = aabb.getPoint(CENTER, config.head.mouthY)
            val width = aabb.convertWidth(config.head.mouthConfig.getSimpleWidth(head.mouth.width))
            val height = aabb.convertHeight(config.head.mouthConfig.getHeight(head.mouth))
            val mouthAabb = AABB.fromCenter(center, Size2d(width, height))
            val option = NoBorder(Color.Black.toRender())

            renderer.renderRectangle(mouthAabb, option)

            visualizeBeard(renderer, config, aabb, head, head.mouth.beard)
        }

        is FemaleMouth -> visualizeFemaleMouth(renderer, config, aabb, head.mouth)
    }
}

private fun visualizeFemaleMouth(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    mouth: FemaleMouth,
) {
    val options = NoBorder(mouth.color.toRender())
    val width = config.head.mouthConfig.getSimpleWidth(mouth.width)
    val halfHeight = config.head.mouthConfig.femaleHeight * 0.5f
    val (left, right) = aabb.getMirroredPoints(width, config.head.mouthY)
    val (topLeft, topRight) =
        aabb.getMirroredPoints(width * 0.5f, config.head.mouthY - halfHeight)
    val (bottomLeft, bottomRight) =
        aabb.getMirroredPoints(width * 0.5f, config.head.mouthY + halfHeight)
    val cupidsBow = aabb.getPoint(CENTER, config.head.mouthY - halfHeight * 0.5f)

    val polygon = Polygon2d(listOf(left, bottomLeft, bottomRight, right, topRight, cupidsBow, topLeft))
    renderer.renderPolygon(polygon, options)
}

