package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.OnWorld
import at.orchaldir.gm.core.model.util.VitalStatusType
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.core.reducer.util.testAllowedVitalStatusTypes
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MoonTest {

    val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Moon(MOON_ID_0)),
            Storage(Plane(PLANE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Test allowed vital status types`() {
            testAllowedVitalStatusTypes(
                state,
                mapOf(
                    VitalStatusType.Abandoned to false,
                    VitalStatusType.Alive to true,
                    VitalStatusType.Closed to false,
                    VitalStatusType.Dead to false,
                    VitalStatusType.Destroyed to true,
                    VitalStatusType.Vanished to true,
                ),
            ) { status ->
                Moon(MOON_ID_0, status = status)
            }
        }

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(Moon(MOON_ID_0))

            assertIllegalArgument("Requires unknown Moon 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val moon = Moon(MOON_ID_0, NAME, plane = PLANE_ID_0)
            val action = UpdateAction(moon)

            assertEquals(moon, REDUCER.invoke(state, action).first.getMoonStorage().get(MOON_ID_0))
        }

        @Test
        fun `Days per quarter is too small`() {
            val moon = Moon(MOON_ID_0, NAME, daysPerQuarter = 0)
            val action = UpdateAction(moon)

            assertIllegalArgument("Days per quarter most be greater than 0!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use unknown plane`() {
            val action = UpdateAction(Moon(MOON_ID_0, plane = UNKNOWN_PLANE_ID))

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown element as position`() {
            val moon = Moon(MOON_ID_0, position = OnWorld(UNKNOWN_WORLD_ID))
            val action = UpdateAction(moon)

            assertIllegalArgument("Requires unknown World 99 as position!") { REDUCER.invoke(state, action) }
        }
    }

}