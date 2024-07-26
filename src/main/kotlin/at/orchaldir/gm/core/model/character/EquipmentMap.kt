package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMap(val map: Map<EquipmentType, ItemTemplateId>) {

    fun contains(itemTemplate: ItemTemplateId) = map.containsValue(itemTemplate)
    fun contains(type: EquipmentType) = map.containsKey(type)
}