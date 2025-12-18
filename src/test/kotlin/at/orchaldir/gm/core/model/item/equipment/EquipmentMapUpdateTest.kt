package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentMapUpdateTest {

    @Nested
    inner class ApplyToTest {

        @Test
        fun `Test empty update`() {
            val input = EquipmentIdMap.fromId(EQUIPMENT_ID_0, null, BodySlot.Head)
            val update = EquipmentMapUpdate()

            assertEquals(input, update.applyTo(input))
        }

        @Test
        fun `Add another equipment`() {
            val input = EquipmentIdMap.fromId(EQUIPMENT_ID_0, null, BodySlot.Head)
            val update = EquipmentMapUpdate(added = EquipmentIdMap.fromId(EQUIPMENT_ID_1, null, BodySlot.Foot))
            val result = EquipmentIdMap.fromSlotToIdMap(mapOf(BodySlot.Head to EQUIPMENT_ID_0, BodySlot.Foot to EQUIPMENT_ID_1))

            assertEquals(result, update.applyTo(input))
        }
    }

}