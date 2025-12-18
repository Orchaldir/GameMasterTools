package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap.Companion.fromSlotAsValueMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate.Companion.calculateUpdate
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
    val emptyUpdate = EquipmentMapUpdate()

    @Nested
    inner class ApplyToTest {

        @Test
        fun `Test empty update`() {
            assertEquals(map0, emptyUpdate.applyTo(map0))
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
        fun `Update between to empty maps is empty`() {
            assertEquals(emptyUpdate, calculateUpdate(emptyMap, emptyMap))
        }

        @Test
        fun `Update between to identical maps is empty`() {
            assertEquals(emptyUpdate, calculateUpdate(map0, map0))
            assertEquals(emptyUpdate, calculateUpdate(map1, map1))
            assertEquals(emptyUpdate, calculateUpdate(map01, map01))
            assertEquals(emptyUpdate, calculateUpdate(twice0, twice0))
        }

        @Test
        fun `Update adds first equipment`() {
            assertAdd(emptyMap, map0, map0)
            assertAdd(emptyMap, map1, map1)
        }

        @Test
        fun `Update adds second equipment`() {
            assertAdd(map0, map01, map1)
            assertAdd(map1, map01, map0)
        }

        private fun assertAdd(
            from: EquipmentIdMap,
            to: EquipmentIdMap,
            added: EquipmentIdMap,
        ) {
            val update = EquipmentMapUpdate(added = added)

            assertEquals(update, calculateUpdate(from, to))
        }
    }

}