package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.RenderState

fun visualizeGroup(
    config: RenderConfig,
    appearances: List<Appearance>,
    equipped: List<Equipment> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val N = appearances.size
    val sizes = appearances.map { it.getSize() }
    val maxSize = sizes.maxBy { it.value }
    val groupSize = Size2d(maxSize * N + config.padding * 2, maxSize + config.padding * 2)
    val builder = SvgBuilder(groupSize)
    var start = Point2d(config.padding, config.padding)

    appearances.forEach { appearance ->
        val size = appearance.getSize2d()
        val aabb = AABB(start, size)
        val state = RenderState(aabb, config, builder, renderFront, equipped)

        visualizeAppearance(state, appearance)

        start += Point2d(size.width, 0.0f)
    }

    return builder.finish()
}
