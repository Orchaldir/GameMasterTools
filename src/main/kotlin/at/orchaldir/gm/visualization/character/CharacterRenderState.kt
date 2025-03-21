package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.item.equipment.EquipmentData
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

data class CharacterRenderState(
    val aabb: AABB,
    val config: CharacterRenderConfig,
    val renderer: MultiLayerRenderer,
    val renderFront: Boolean,
    val equipped: List<EquipmentData>,
) {

    fun getBeardLayer() = if (renderFront) {
        ABOVE_EQUIPMENT_LAYER
    } else {
        -ABOVE_EQUIPMENT_LAYER
    }

    fun getSideOffset(offset: Factor) = if (renderFront) {
        offset
    } else {
        -offset
    }

    fun hasEquipped(slot: EquipmentSlot) = equipped.any { it.slots().contains(slot) }
}