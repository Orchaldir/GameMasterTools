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
        val map0 = EquipmentIdMap.fromId(EQUIPMENT_ID_0, null, BodySlot.Head)
        val map1 = EquipmentIdMap.fromId(EQUIPMENT_ID_1, null, BodySlot.Foot)
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
    }

}