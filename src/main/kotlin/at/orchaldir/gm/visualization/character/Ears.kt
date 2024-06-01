package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig

data class EarConfig(
    private val roundRadius: SizeConfig,
) {
    fun getRoundRadius(aabb: AABB, size: Size) = Distance(aabb.size.height * roundRadius.convert(size))
}

fun visualizeEars(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.ears) {
        NoEars -> doNothing()
        is NormalEars -> visualizeNormalEars(renderer, config, aabb, head.ears.shape, head.ears.size, head.skin)
    }
}

private fun visualizeNormalEars(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    shape: EarShape,
    size: Size,
    skin: Skin,
) {
    val option = config.getOptions(skin)

    when (shape) {
        EarShape.PointedSideways -> TODO()
        EarShape.PointedUpwards -> TODO()
        EarShape.Round -> visualizeRoundEars(config, renderer, aabb, size, option)
    }
}

private fun visualizeRoundEars(
    config: RenderConfig,
    renderer: Renderer,
    aabb: AABB,
    size: Size,
    option: RenderOptions,
) {
    val (left, right) = aabb.getMirroredPoints(Factor(1.0f), config.head.earY)
    val radius = config.head.ears.getRoundRadius(aabb, size)

    renderer.renderCircle(left, radius, option)
    renderer.renderCircle(right, radius, option)
}