package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
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
    }
}
