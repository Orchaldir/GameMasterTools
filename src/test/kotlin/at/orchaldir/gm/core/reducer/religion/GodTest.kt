package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.CALENDAR0
import at.orchaldir.gm.GOD_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteGod
import at.orchaldir.gm.core.action.UpdateGod
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val god0 = God(GOD_ID_0)
private val STATE = State(
    listOf(
        Storage(CALENDAR0),
        Storage(god0),
    )
)

class GodTest {

    @Nested
    inner class DeleteTest {
        val action = DeleteGod(GOD_ID_0)

        @Test
        fun `Can delete an existing god`() {
            assertEquals(0, REDUCER.invoke(STATE, action).first.getGodStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown God 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateGod(God(GOD_ID_0))
            val state = STATE.removeStorage(GOD_ID_0)

            assertIllegalArgument("Requires unknown God 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update a god`() {
            val god = God(GOD_ID_0, "Test")
            val action = UpdateGod(god)

            assertEquals(god, REDUCER.invoke(STATE, action).first.getGodStorage().get(GOD_ID_0))
        }
    }

}