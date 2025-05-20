package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteEquipment
import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val ITEM = Equipment(EQUIPMENT_ID_0, NAME)
private val STATE = State(listOf(Storage(Equipment(EQUIPMENT_ID_0)), Storage(Material(MATERIAL_ID_0))))
private val EQUIPMENT_MAP = EquipmentMap(EQUIPMENT_ID_0, BodySlot.Head)

class EquipmentTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing id`() {
            val action = DeleteEquipment(EQUIPMENT_ID_0)

            assertEquals(
                0,
                REDUCER.invoke(STATE, action).first.getEquipmentStorage().getSize()
            )
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteEquipment(EQUIPMENT_ID_0)

            assertIllegalArgument("Requires unknown Equipment 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete, if an instanced item exist`() {
            val action = DeleteEquipment(EQUIPMENT_ID_0)
            val state =
                STATE.updateStorage(
                    Storage(
                        Character(
                            CHARACTER_ID_0,
                            equipmentMap = EQUIPMENT_MAP
                        )
                    )
                )

            assertIllegalArgument("Cannot delete Equipment 0, because it is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateEquipment(ITEM)

            assertIllegalArgument("Requires unknown Equipment 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot change equipment type while equipped`() {
            val oldItem = Equipment(EQUIPMENT_ID_0, data = Pants(main = FillItemPart(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_0)))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER_ID_0, equipmentMap = EQUIPMENT_MAP)),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = UpdateEquipment(newItem)

            assertIllegalArgument("Cannot change equipment 0 while it is equipped") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Can change equipment details while equipped`() {
            val oldItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_0)))
            val newItem = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_1)))
            val state = State(
                listOf(
                    Storage(oldItem),
                    Storage(Character(CHARACTER_ID_0, equipmentMap = EQUIPMENT_MAP)),
                    Storage(listOf(Material(MATERIAL_ID_0), Material(MATERIAL_ID_1))),
                )
            )
            val action = UpdateEquipment(newItem)

            assertEquals(newItem, REDUCER.invoke(state, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0))
        }

        @Test
        fun `Material must exist`() {
            val item = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_1)))
            val action = UpdateEquipment(item)

            assertIllegalArgument("Requires unknown Material 1!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Update template`() {
            val action = UpdateEquipment(ITEM)

            assertEquals(
                ITEM,
                REDUCER.invoke(STATE, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0)
            )
        }

        @Test
        fun `Update template with material`() {
            val item = Equipment(EQUIPMENT_ID_0, data = Shirt(main = FillItemPart(MATERIAL_ID_0)))
            val state = State(
                listOf(
                    Storage(ITEM),
                    Storage(Material(MATERIAL_ID_0)),
                )
            )
            val action = UpdateEquipment(item)

            assertEquals(item, REDUCER.invoke(state, action).first.getEquipmentStorage().get(EQUIPMENT_ID_0))
        }
    }

}