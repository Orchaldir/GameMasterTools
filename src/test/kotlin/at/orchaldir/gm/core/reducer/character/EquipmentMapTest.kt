package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.CHARACTER_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_0
import at.orchaldir.gm.EQUIPMENT_ID_1
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateEquipmentOfCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EquipmentMapTest {

    private val equipmentMap = EquipmentMap(EQUIPMENT_ID_0, BodySlot.Head)
    private val action = UpdateEquipmentOfCharacter(CHARACTER_ID_0, equipmentMap)

    @Test
    fun `Update equipment`() {
        val state = State(
            listOf(
                Storage(listOf(Character(CHARACTER_ID_0))),
                Storage(listOf(Equipment(EQUIPMENT_ID_0, data = Hat()))),
            )
        )
        val result = REDUCER.invoke(state, action).first

        assertEquals(equipmentMap, result.getCharacterStorage().getOrThrow(CHARACTER_ID_0).equipmentMap)
    }

    @Test
    fun `Cannot update unknown character`() {
        val state = State(Storage(Equipment(EQUIPMENT_ID_0)))

        assertIllegalArgument("Requires unknown Character 0!") { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot use unknown equipment`() {
        val state = State(Storage(listOf(Character(CHARACTER_ID_0))))

        assertIllegalArgument("Requires unknown Equipment 0!") { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot use equipment with wrong slots`() {
        val state = State(
            listOf(
                Storage(listOf(Character(CHARACTER_ID_0))),
                Storage(listOf(Equipment(EQUIPMENT_ID_0, data = Dress()))),
            )
        )

        assertIllegalArgument("Equipment 0 uses wrong slots!") { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot occupy equipment slot twice`() {
        val state = State(
            listOf(
                Storage(listOf(Character(CHARACTER_ID_0))),
                Storage(
                    listOf(
                        Equipment(EQUIPMENT_ID_0, data = Dress()),
                        Equipment(EQUIPMENT_ID_1, data = Shirt())
                    )
                ),
            )
        )
        val equipmentMap = EquipmentMap(
            mapOf(
                EQUIPMENT_ID_0 to setOf(setOf(BodySlot.Bottom, BodySlot.InnerTop)),
                EQUIPMENT_ID_1 to setOf(setOf(BodySlot.InnerTop)),
            )
        )
        val action = UpdateEquipmentOfCharacter(CHARACTER_ID_0, equipmentMap)

        assertIllegalArgument("Body slot InnerTop is occupied multiple times!") { REDUCER.invoke(state, action) }
    }
}