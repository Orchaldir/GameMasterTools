package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EquipmentElementMap
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

data class CharacterRenderState(
    val state: State,
    val aabb: AABB,
    val config: CharacterRenderConfig,
    val renderer: MultiLayerRenderer,
    val renderFront: Boolean,
    val equipped: EquipmentElementMap,
) {

    fun getBeardLayer() = getLayer(ABOVE_EQUIPMENT_LAYER)
    fun getTailLayer() = getLayer(-ABOVE_EQUIPMENT_LAYER)

    fun getLayer(layer: Int) = renderer.getLayer(getLayerIndex(layer))

    fun getLayerIndex(layer: Int) = if (renderFront) {
        layer
    } else {
        -layer
    }

    fun getSideOffset(offset: Factor) = if (renderFront) {
        offset
    } else {
        -offset
    }

}