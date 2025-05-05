package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeGroup(
    state: State,
    config: CharacterRenderConfig,
    appearances: List<Appearance>,
    equipped: EquipmentMap<EquipmentData> = EquipmentMap(),
    renderFront: Boolean = true,
): Svg {
    val number = appearances.size
    val paddedSizeMap = appearances
        .associateWith { calculatePaddedSize(config, it) }
    val maxSize = paddedSizeMap
        .values
        .maxBy { it.baseSize.height }
        .getFullSize()
    val groupSize = maxSize.copy(width = maxSize.width * number)
    val builder = SvgBuilder(groupSize)
    var start = Point2d(config.padding, Distance.fromMeters(groupSize.height) - config.padding)

    appearances.forEach { appearance ->
        val size = appearance.getSize2d()
        val aabb = AABB(start - Point2d(0.0f, size.height), size)
        val renderState = CharacterRenderState(state, aabb, config, builder, renderFront, equipped)

        visualizeAppearance(renderState, appearance, paddedSizeMap.getValue(appearance))

        start += Point2d(size.width, 0.0f)
    }

    return builder.finish()
}
