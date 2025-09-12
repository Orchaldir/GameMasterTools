package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateMoon
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.OnWorld
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MoonTest {

    val state = State(
        listOf(
            Storage(Moon(MOON_ID_0)),
            Storage(Plane(PLANE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateMoon(Moon(MOON_ID_0))

            assertIllegalArgument("Requires unknown Moon 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val moon = Moon(MOON_ID_0, NAME, plane = PLANE_ID_0)
            val action = UpdateMoon(moon)

            assertEquals(moon, REDUCER.invoke(state, action).first.getMoonStorage().get(MOON_ID_0))
        }

        @Test
        fun `Days per quarter is too small`() {
            val moon = Moon(MOON_ID_0, NAME, daysPerQuarter = 0)
            val action = UpdateMoon(moon)

            assertIllegalArgument("Days per quarter most be greater than 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown plane`() {
            val action = UpdateMoon(Moon(MOON_ID_0, plane = UNKNOWN_PLANE_ID))

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown element as position`() {
            val moon = Moon(MOON_ID_0, position = OnWorld(UNKNOWN_WORLD_ID))
            val action = UpdateMoon(moon)

            assertIllegalArgument("Requires unknown World 99 as position!") { REDUCER.invoke(state, action) }
        }
    }

}