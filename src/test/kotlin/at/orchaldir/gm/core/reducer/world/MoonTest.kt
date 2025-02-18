package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.MOON_ID_0
import at.orchaldir.gm.core.action.DeleteMoon
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoonTest {

    val state = State(Storage(Moon(MOON_ID_0)))

    @Nested
    inner class DeleteTest {
        val action = DeleteMoon(MOON_ID_0)

        @Test
        fun `Can delete an existing moon`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getMoonStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMoon(Moon(MOON_ID_0))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val moon = Moon(MOON_ID_0, "Test")
            val action = UpdateMoon(moon)

            assertEquals(moon, REDUCER.invoke(state, action).first.getMoonStorage().get(MOON_ID_0))
        }

        @Test
        fun `Days per quarter is too small`() {
            val moon = Moon(MOON_ID_0, "Test", daysPerQuarter = 0)
            val action = UpdateMoon(moon)

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }
    }

}