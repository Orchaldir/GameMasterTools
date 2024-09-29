package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.core.model.item.EquipmentSlot
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.character.ABOVE_EQUIPMENT_LAYER

data class RenderState(
    val aabb: AABB,
    val config: RenderConfig,
    val renderer: LayerRenderer,
    val renderFront: Boolean,
    val equipped: List<Equipment>,
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
