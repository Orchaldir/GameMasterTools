package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
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
    }

}