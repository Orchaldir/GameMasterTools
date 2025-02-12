package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateEquipment
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = CharacterId(0)
private val ITEM0 = EquipmentId(0)
private val ITEM1 = EquipmentId(1)

class EquipmentTest {

    private val equipmentMap = EquipmentMap(mapOf(EquipmentDataType.Hat to ITEM0))
    private val action = UpdateEquipment(ID0, equipmentMap)

    @Test
    fun `Update equipment`() {
        val state = State(
            listOf(
                Storage(listOf(Character(ID0))),
                Storage(listOf(ItemTemplate(ITEM0, equipment = Hat()))),
            )
        )

        val result = REDUCER.invoke(state, action).first

        assertEquals(equipmentMap, result.getCharacterStorage().getOrThrow(ID0).equipmentMap)
    }

    @Test
    fun `Cannot update unknown character`() {
        val state = State()

        assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot use unknown item template`() {
        val state = State(Storage(listOf(Character(ID0))))

        assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot use item template of wrong type`() {
        val state = State(
            listOf(
                Storage(listOf(Character(ID0))),
                Storage(listOf(ItemTemplate(ITEM0, equipment = Dress()))),
            )
        )

        assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
    }

    @Test
    fun `Cannot occupy equipment slot twice`() {
        val state = State(
            listOf(
                Storage(listOf(Character(ID0))),
                Storage(
                    listOf(
                        ItemTemplate(ITEM0, equipment = Dress()),
                        ItemTemplate(ITEM1, equipment = Shirt())
                    )
                ),
            )
        )
        val equipmentMap = EquipmentMap(mapOf(EquipmentDataType.Dress to ITEM0, EquipmentDataType.Shirt to ITEM1))
        val action = UpdateEquipment(ID0, equipmentMap)

        assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
    }

}