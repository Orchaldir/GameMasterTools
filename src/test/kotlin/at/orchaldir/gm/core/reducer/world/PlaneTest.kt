package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.model.world.plane.ReflectivePlane
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class PlaneTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(listOf(Plane(PLANE_ID_0), Plane(PLANE_ID_1))),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeletePlane(PLANE_ID_0)

        @Test
        fun `Can delete an existing plane`() {
            assertEquals(1, REDUCER.invoke(state, action).first.getPlaneStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeletePlane(UNKNOWN_PLANE_ID)

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {
        val action = UpdatePlane(Plane(PLANE_ID_0))

        @Test
        fun `Cannot update unknown id`() {
            assertIllegalArgument("Requires unknown Plane 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Reflected plane must exist`() {
            val plane = Plane(PLANE_ID_0, purpose = ReflectivePlane(UNKNOWN_PLANE_ID))
            val action = UpdatePlane(plane)

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update is valid`() {
            val plane = Plane(PLANE_ID_0, "Test")
            val action = UpdatePlane(plane)

            assertEquals(plane, REDUCER.invoke(state, action).first.getPlaneStorage().get(PLANE_ID_0))
        }
    }

}