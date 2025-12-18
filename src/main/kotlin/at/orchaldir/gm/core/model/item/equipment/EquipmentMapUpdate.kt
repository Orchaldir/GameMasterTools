package at.orchaldir.gm.core.model.item.equipment

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentMapUpdate(
    val removed: Set<Set<BodySlot>> = emptySet(),
    val added: EquipmentIdMap = EquipmentIdMap(),
) {
    companion object {
        fun calculateUpdate(from: EquipmentIdMap, to: EquipmentIdMap): EquipmentMapUpdate {
            val removed: MutableSet<Set<BodySlot>> = mutableSetOf()

            from.getEquipmentWithSlotSets().forEach { (data, sets) ->
                sets.forEach { set ->
                    if (to.getEquipment(set) != data) {
                        removed.add(set)
                    }
                }
            }

            val added: MutableList<EquipmentMapEntry<EquipmentIdPair>> = mutableListOf()

            to.getEquipmentWithSlotSets().forEach { (data, sets) ->
                sets.forEach { set ->
                    if (from.getEquipment(set) != data) {
                        added.add(EquipmentMapEntry.from(data, set))
                    }
                }
            }

            return EquipmentMapUpdate(removed, EquipmentIdMap(added))
        }
    }

    fun applyTo(map: EquipmentIdMap): EquipmentIdMap {
        val updatedList = map.getEquipmentWithSlotSets().mapNotNull { (data, sets) ->
            val addedSets = added.getSets(data) ?: emptySet()
            val newSets = sets.filter { set -> !removed.contains(set) }
                .toSet() + addedSets

            if (newSets.isEmpty()) {
                null
            } else {
                EquipmentMapEntry(data, newSets)
            }
        }
        val addedList = added.getEquipmentWithSlotSets().filter { entry ->
            !map.contains(entry.data)
        }

        return EquipmentIdMap(updatedList + addedList)
    }
}
