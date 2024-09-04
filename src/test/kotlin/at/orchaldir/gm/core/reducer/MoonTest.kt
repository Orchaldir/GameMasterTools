package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.moon.Moon
import at.orchaldir.gm.core.model.moon.MoonId
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private val ID0 = MoonId(0)

class MoonTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an existing moon`() {
            val state = State(Storage(Moon(ID0)))
            val action = DeleteMoon(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getMoonStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeleteMoon(ID0)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMoon(Moon(ID0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(Moon(ID0)))
            val moon = Moon(ID0, "Test")
            val action = UpdateMoon(moon)

            assertEquals(moon, REDUCER.invoke(state, action).first.getMoonStorage().get(ID0))
        }

        @Test
        fun `Days per quarter is too small`() {
            val state = State(Storage(Moon(ID0)))
            val moon = Moon(ID0, "Test", daysPerQuarter = 0)
            val action = UpdateMoon(moon)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

}