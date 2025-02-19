package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.PlanarLanguage
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.world.plane.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class PlaneTest {

    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(God(GOD_ID_0)),
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
        fun `Cannot delete the plane linked to a demiplane`() {
            val plane = Plane(PLANE_ID_1, purpose = Demiplane(PLANE_ID_0))
            val newState = state.updateStorage(Storage(listOf(Plane(PLANE_ID_0), plane)))

            assertIllegalArgument("Plane 0 is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete a reflected plane`() {
            val plane = Plane(PLANE_ID_1, purpose = ReflectivePlane(PLANE_ID_0))
            val newState = state.updateStorage(Storage(listOf(Plane(PLANE_ID_0), plane)))

            assertIllegalArgument("Plane 0 is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete the origin of a language`() {
            val newState = state.updateStorage(Storage(Language(LANGUAGE_ID_0, origin = PlanarLanguage(PLANE_ID_0))))

            assertIllegalArgument("Plane 0 is used!") { REDUCER.invoke(newState, action) }
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
        fun `A demiplane requires another plane`() {
            val plane = Plane(PLANE_ID_0, purpose = Demiplane(UNKNOWN_PLANE_ID))
            val action = UpdatePlane(plane)

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A heart plane requires a god`() {
            val plane = Plane(PLANE_ID_0, purpose = HeartPlane(UNKNOWN_GOD_ID))
            val action = UpdatePlane(plane)

            assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `A god can only have 1 heart plane`() {
            val plane0 = Plane(PLANE_ID_0, purpose = HeartPlane(GOD_ID_0))
            val plane1 = Plane(PLANE_ID_1, purpose = HeartPlane(GOD_ID_0))
            val newState = state.updateStorage(Storage(listOf(plane0, Plane(PLANE_ID_1))))
            val action = UpdatePlane(plane1)

            assertIllegalArgument("God 0 already has a heart plane!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Update a heart plane`() {
            val plane0 = Plane(PLANE_ID_0, purpose = HeartPlane(GOD_ID_0))
            val plane1 = Plane(PLANE_ID_0, "Test", purpose = HeartPlane(GOD_ID_0))
            val newState = state.updateStorage(Storage(listOf(plane0, Plane(PLANE_ID_1))))
            val action = UpdatePlane(plane1)

            assertEquals(plane1, REDUCER.invoke(newState, action).first.getPlaneStorage().get(PLANE_ID_0))
        }

        @Test
        fun `Update is valid`() {
            val plane = Plane(PLANE_ID_0, "Test")
            val action = UpdatePlane(plane)

            assertEquals(plane, REDUCER.invoke(state, action).first.getPlaneStorage().get(PLANE_ID_0))
        }
    }

}