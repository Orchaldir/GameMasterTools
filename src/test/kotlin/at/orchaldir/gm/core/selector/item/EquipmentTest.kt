package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.UniqueEquipment
import at.orchaldir.gm.core.model.culture.fashion.ClothingFashion
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.model.util.OneOrNone
import at.orchaldir.gm.core.selector.item.equipment.canDeleteEquipment
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
        fun `Cannot delete a equipment that is equipped a characeter`() {
            val map = EquipmentMap
                .from(BodySlot.Head, EQUIPMENT_ID_0, COLOR_SCHEME_ID_0)
            val character = Character(CHARACTER_ID_0, equipped = UniqueEquipment(map))
            val newState = state.updateStorage(Storage(character))

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete a equipment that is equipped by a template`() {
            val map = EquipmentMap
                .from(BodySlot.Head, EQUIPMENT_ID_0, COLOR_SCHEME_ID_0)
            val template =
                CharacterTemplate(CHARACTER_TEMPLATE_ID_0, race = RACE_ID_0, equipped = UniqueEquipment(map))
            val newState = state.updateStorage(Storage(template))

            failCanDelete(newState, CHARACTER_TEMPLATE_ID_0)
        }

        @Test
        fun `Cannot delete a equipment that is part of a fashion`() {
            val map = mapOf(EquipmentDataType.Pants to OneOrNone(EQUIPMENT_ID_0))
            val fashion = Fashion(FASHION_ID_0, clothing = ClothingFashion(equipmentRarityMap = map))
            val newState = state.updateStorage(Storage(fashion))

            failCanDelete(newState, FASHION_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(EQUIPMENT_ID_0).addId(blockingId),
                state.canDeleteEquipment(EQUIPMENT_ID_0)
            )
        }
    }

}