package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = RiverId(0)

class RiverTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing moon`() {
            val state = State(Storage(River(ID0)))
            val action = DeleteRiver(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getRiverStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteRiver(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateRiver(River(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(River(ID0)))
            val moon = River(ID0, "Test")
            val action = UpdateRiver(moon)

            assertEquals(moon, REDUCER.invoke(state, action).first.getRiverStorage().get(ID0))
        }
    }

}