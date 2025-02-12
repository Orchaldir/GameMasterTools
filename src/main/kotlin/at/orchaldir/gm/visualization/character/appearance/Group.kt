package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeGroup(
    config: CharacterRenderConfig,
    appearances: List<Appearance>,
    equipped: List<Equipment> = emptyList(),
    renderFront: Boolean = true,
): Svg {
    val number = appearances.size
    val sizes = appearances.map { it.getSize() }
    val maxSize = sizes.maxBy { it.toMeters() }
    val groupSize = Size2d(maxSize * number + config.padding * 2, maxSize + config.padding * 2)
    val builder = SvgBuilder(groupSize)
    var start = Point2d(config.padding, Distance.fromMeters(groupSize.height) - config.padding)

    appearances.forEach { appearance ->
        val size = appearance.getSize2d()
        val aabb = AABB(start - Point2d(0.0f, size.height), size)
        val state = CharacterRenderState(aabb, config, builder, renderFront, equipped)

        visualizeAppearance(state, appearance)

        start += Point2d(size.width, 0.0f)
    }

    return builder.finish()
}
