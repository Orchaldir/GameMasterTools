package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.EquippedEquipment
import at.orchaldir.gm.core.model.character.EquippedUniform
import at.orchaldir.gm.core.model.character.ModifiedUniform
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMapUpdate
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UniformTest {

    private val STATE = State(
        listOf(
            Storage(CALENDAR0),
            Storage(ColorScheme(COLOR_SCHEME_ID_0)),
            Storage(Equipment(EQUIPMENT_ID_0, data = Helmet())),
            Storage(Uniform(UNIFORM_ID_0)),
            Storage(Spell(SPELL_ID_0)),
        ),
        data = Data(Economy(standardsOfLiving = listOf(StandardOfLiving(STANDARD_ID_0)))),
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Uniform(UNKNOWN_UNIFORM_ID))

            assertIllegalArgument("Requires unknown Uniform 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update uniform with unknown equipment`() {
            val equipmentMap = EquipmentMap.from(BodySlot.Head, UNKNOWN_EQUIPMENT_ID, COLOR_SCHEME_ID_0)
            val action = UpdateAction(Uniform(UNIFORM_ID_0, equipped = EquippedEquipment(equipmentMap)))

            assertIllegalArgument("Requires unknown Equipment 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update uniform based on itself`() {
            val action = UpdateAction(Uniform(UNIFORM_ID_0, equipped = EquippedUniform(UNIFORM_ID_0)))

            assertIllegalArgument("Uniform 0 is based on itself!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update uniform modifying itself`() {
            val equipped = ModifiedUniform(UNIFORM_ID_0, EquipmentMapUpdate())
            val action = UpdateAction(Uniform(UNIFORM_ID_0, equipped = equipped))

            assertIllegalArgument("Uniform 0 is based on itself!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Success`() {
            val equipmentMap = EquipmentMap.from(BodySlot.Head, EQUIPMENT_ID_0, COLOR_SCHEME_ID_0)
            val uniform = Uniform(UNIFORM_ID_0, equipped = EquippedEquipment(equipmentMap))
            val action = UpdateAction(uniform)

            assertEquals(uniform, REDUCER.invoke(STATE, action).first.getUniformStorage().get(UNIFORM_ID_0))
        }
    }

}