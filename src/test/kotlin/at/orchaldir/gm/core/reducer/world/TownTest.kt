package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = TownId(0)

class TownTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing Town`() {
            val state = State(Storage(Town(ID0)))
            val action = DeleteTown(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getTownStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteTown(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateTown(Town(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Town(ID0)))
            val town = Town(ID0, "Test")
            val action = UpdateTown(town)

            assertEquals(town, REDUCER.invoke(state, action).first.getTownStorage().get(ID0))
        }
    }

}