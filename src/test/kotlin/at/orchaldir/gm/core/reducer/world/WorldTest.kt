package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InPlane
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorldTest {

    val state = State(
        listOf(
            Storage(World(WORLD_ID_0)),
            Storage(Plane(PLANE_ID_0)),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(World(WORLD_ID_0))

            assertIllegalArgument("Requires unknown World 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Update is valid`() {
            val moon = World(WORLD_ID_0, NAME, position = InPlane(PLANE_ID_0))
            val action = UpdateAction(moon)

            assertEquals(moon, REDUCER.invoke(state, action).first.getWorldStorage().get(WORLD_ID_0))
        }

        @Test
        fun `Cannot use an unknown element as position`() {
            val moon = World(WORLD_ID_0, position = InPlane(UNKNOWN_PLANE_ID))
            val action = UpdateAction(moon)

            assertIllegalArgument("Requires unknown Plane 99 as position!") { REDUCER.invoke(state, action) }
        }
    }

}