package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsValueMap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentMapUpdateTest {
    val sets = setOf(setOf(BodySlot.Foot), setOf(BodySlot.Head))
    val emptyMap = EquipmentIdMap()
    val map0 = EquipmentIdMap.from(BodySlot.Head, EQUIPMENT_ID_0)
    val map1 = EquipmentIdMap.from(BodySlot.Foot, EQUIPMENT_ID_1)
    val map01 = EquipmentIdMap.fromSlotToIdMap(mapOf(BodySlot.Head to EQUIPMENT_ID_0, BodySlot.Foot to EQUIPMENT_ID_1))
    val twice0 = fromSlotAsValueMap(mapOf(EquipmentIdPair(EQUIPMENT_ID_0, null) to sets))

    @Nested
    inner class ApplyToTest {

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

            assertEquals(twice0, update.applyTo(map0))
        }

        @Test
        fun `Remove one of 2 equipments`() {
            val update = EquipmentMapUpdate(setOf(setOf(BodySlot.Foot)))

            assertEquals(map0, update.applyTo(map01))
        }

        @Test
        fun `Remove one of 2 instances`() {
            val update = EquipmentMapUpdate(setOf(setOf(BodySlot.Foot)))

            assertEquals(map0, update.applyTo(twice0))
        }
    }

    @Nested
    inner class CalculateUpdateTest {
        @Test
        fun `Test update between to empty maps`() {
            assertEquals(EquipmentMapUpdate(), EquipmentMapUpdate.calculateUpdate(emptyMap, emptyMap))
        }
    }

}