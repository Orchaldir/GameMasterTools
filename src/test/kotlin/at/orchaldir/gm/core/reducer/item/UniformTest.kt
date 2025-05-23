package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteUniform
import at.orchaldir.gm.core.action.UpdateUniform
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.util.GenderMap
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
            Storage(Equipment(EQUIPMENT_ID_0)),
            Storage(Uniform(UNIFORM_ID_0)),
            Storage(Spell(SPELL_ID_0)),
        ),
        data = Data(Economy(standardsOfLiving = listOf(StandardOfLiving(STANDARD_ID_0)))),
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteUniform(UNIFORM_ID_0)

        @Test
        fun `Can delete an existing uniform`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getUniformStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Uniform 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a uniform used by a job`() {
            val state = STATE.updateStorage(Storage(Job(JOB_ID_0, uniforms = GenderMap(UNIFORM_ID_0))))

            assertIllegalArgument("Cannot delete Uniform 0, because it is used!") {
                REDUCER.invoke(state, action)
            }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateUniform(Uniform(UNKNOWN_UNIFORM_ID))

            assertIllegalArgument("Requires unknown Uniform 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Cannot update uniform with unknown equipment`() {
            val equipmentMap = EquipmentMap.fromId(UNKNOWN_EQUIPMENT_ID, COLOR_SCHEME_ID_0, BodySlot.Head)
            val action = UpdateUniform(Uniform(UNIFORM_ID_0, equipmentMap = equipmentMap))

            assertIllegalArgument("Requires unknown Equipment 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Success`() {
            val equipmentMap = EquipmentMap.fromId(EQUIPMENT_ID_0, COLOR_SCHEME_ID_0, BodySlot.Head)
            val uniform = Uniform(UNIFORM_ID_0, equipmentMap = equipmentMap)
            val action = UpdateUniform(uniform)

            assertEquals(uniform, REDUCER.invoke(STATE, action).first.getUniformStorage().get(UNIFORM_ID_0))
        }
    }

}