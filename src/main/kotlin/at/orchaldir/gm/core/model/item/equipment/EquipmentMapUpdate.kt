package at.orchaldir.gm.core.model.item.equipment

data class EquipmentMapUpdate(
    val removed: Set<Set<BodySlot>> = emptySet(),
    val added: EquipmentIdMap = EquipmentIdMap(),
) {

    fun applyTo(map: EquipmentIdMap) = EquipmentIdMap(
        map.getEquipmentWithSlotSets().mapNotNull { (data, sets) ->
            val addedSets = added.getSets(data) ?: emptySet()
            val newSets = sets.filter { set -> !removed.contains(set) }
                .toSet() + addedSets

            if (newSets.isEmpty()) {
                null
            } else {
                EquipmentMapEntry(data, newSets)
            }
        }
    )
}
