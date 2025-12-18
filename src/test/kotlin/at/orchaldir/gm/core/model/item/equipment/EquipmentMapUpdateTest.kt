package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsValueMap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentMapUpdateTest {

    @Nested
    inner class ApplyToTest {
        val map0 = EquipmentIdMap.from(BodySlot.Head, EQUIPMENT_ID_0)
        val map1 = EquipmentIdMap.from(BodySlot.Foot, EQUIPMENT_ID_1)
        val map01 = EquipmentIdMap.fromSlotToIdMap(mapOf(BodySlot.Head to EQUIPMENT_ID_0, BodySlot.Foot to EQUIPMENT_ID_1))

        @Test
        fun `Test empty update`() {
            val update = EquipmentMapUpdate()

            assertEquals(map0, update.applyTo(map0))
        }

        @Test
        fun `Add another equipment`() {
            val update = EquipmentMapUpdate(added = map1)

            assertEquals(map01, update.applyTo(map0))
        }

        @Test
        fun `Add equipment a second time`() {
            val update = EquipmentMapUpdate(added = EquipmentIdMap.from(BodySlot.Foot, EQUIPMENT_ID_0))
            val sets = setOf(setOf(BodySlot.Foot), setOf(BodySlot.Head))
            val result = fromSlotAsValueMap(mapOf(EquipmentIdPair(EQUIPMENT_ID_0, null) to sets))

            assertEquals(result, update.applyTo(map0))
        }
    }

}