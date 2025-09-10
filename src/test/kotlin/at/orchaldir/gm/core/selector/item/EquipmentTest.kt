package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.COLOR_SCHEME_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.FASHION_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.fashion.ClothingFashion
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentTest {

    @Nested
    inner class CanDeleteTest {
        private val equipment = Equipment(EQUIPMENT_ID_0, data = Pants())
        private val state = State(
            listOf(
                Storage(equipment),
            )
        )

        @Test
        fun `Cannot delete a equipment that is equipped`() {
            val map = EquipmentMap
                .fromId(EQUIPMENT_ID_0, COLOR_SCHEME_ID_0, BodySlot.Head)
            val character = Character(CHARACTER_ID_0, equipmentMap = map)
            val newState = state.updateStorage(Storage(character))

            assertCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a equipment that is part of a fashion`() {
            val map = mapOf(EquipmentDataType.Pants to OneOrNone(EQUIPMENT_ID_0))
            val fashion = Fashion(FASHION_ID_0, clothing = ClothingFashion(equipmentRarityMap = map))
            val newState = state.updateStorage(Storage(fashion))

            assertCanDelete(newState, FASHION_ID_0)
        }

        private fun <ID : Id<ID>> assertCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(EQUIPMENT_ID_0).addId(blockingId),
                state.canDeleteEquipment(EQUIPMENT_ID_0)
            )
        }
    }

}