package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.UNKNOWN_EQUIPMENT_ID
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsValueMap


val sets = setOf(setOf(BodySlot.Foot), setOf(BodySlot.Head))
val emptyMap = EquipmentIdMap()
val map0 = EquipmentIdMap.from(BodySlot.Head, EQUIPMENT_ID_0)
val other0 = EquipmentIdMap.from(BodySlot.Foot, EQUIPMENT_ID_0)
val map1 = EquipmentIdMap.from(BodySlot.Foot, EQUIPMENT_ID_1)
val map01 = EquipmentIdMap.fromSlotToIdMap(mapOf(BodySlot.Head to EQUIPMENT_ID_0, BodySlot.Foot to EQUIPMENT_ID_1))
val unknownMap = EquipmentIdMap.from(BodySlot.Foot, UNKNOWN_EQUIPMENT_ID)
val twice0 = fromSlotAsValueMap(mapOf(EquipmentIdPair(EQUIPMENT_ID_0, null) to sets))
val emptyUpdate = EquipmentMapUpdate()
