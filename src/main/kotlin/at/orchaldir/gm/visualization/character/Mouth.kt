package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig

data class MouthConfig(
    private val simpleWidth: SizeConfig,
) {
    fun getSimpleWidth(size: Size) = Factor(simpleWidth.convert(size))
}

fun visualizeMouth(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.mouth) {
        NoMouth -> doNothing()
        is SimpleMouth -> {
            val width = config.head.mouth.getSimpleWidth(head.mouth.width)
            val (left, right) = aabb.getMirroredPoints(width, config.head.mouthY)

            renderer.renderLine(listOf(left, right), config.line)
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
    val width = config.head.mouth.getSimpleWidth(mouth.width)
    val halfHeight = Factor(0.04f)
    val (left, right) = aabb.getMirroredPoints(width, config.head.mouthY)
    val (topLeft, topRight) =
        aabb.getMirroredPoints(width * 0.5f, config.head.mouthY - halfHeight)
    val (bottomLeft, bottomRight) =
        aabb.getMirroredPoints(width * 0.5f, config.head.mouthY + halfHeight)
    val cupidsBow = aabb.getPoint(Factor(0.5f), config.head.mouthY - halfHeight * 0.5f)

    val polygon = Polygon2d(listOf(left, bottomLeft, bottomRight, right, topRight, cupidsBow, topLeft))
    renderer.renderPolygon(polygon, options)
}

