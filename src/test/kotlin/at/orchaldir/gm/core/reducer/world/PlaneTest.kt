package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.InPlane
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.moon.Moon
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
    private val inPlane = InPlane(PLANE_ID_0)

    @Nested
    inner class DeleteTest {
        val action = DeletePlane(PLANE_ID_0)

        @Test
        fun `Can delete an existing plane`() {
            assertEquals(1, REDUCER.invoke(state, action).first.getPlaneStorage().getSize())
        }

        @Test
        fun `Cannot delete the plane linked to a moon`() {
            val newState = state.updateStorage(Storage(Moon(MOON_ID_0, plane = PLANE_ID_0)))

            assertIllegalArgument("Cannot delete Plane 0, because it is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete the plane linked to a demiplane`() {
            val plane = Plane(PLANE_ID_1, purpose = Demiplane(PLANE_ID_0))
            val newState = state.updateStorage(Storage(listOf(Plane(PLANE_ID_0), plane)))

            assertIllegalArgument("Cannot delete Plane 0, because it is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete a reflected plane`() {
            val plane = Plane(PLANE_ID_1, purpose = ReflectivePlane(PLANE_ID_0))
            val newState = state.updateStorage(Storage(listOf(Plane(PLANE_ID_0), plane)))

            assertIllegalArgument("Cannot delete Plane 0, because it is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeletePlane(UNKNOWN_PLANE_ID)

            assertIllegalArgument("Requires unknown Plane 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot delete a plane used as home`() {
            val housingStatus = History<Position>(inPlane)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(Storage(character))

            assertIllegalArgument("Cannot delete Plane 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Cannot delete a plane used as a position`() {
            val plane = Business(BUSINESS_ID_0, position = inPlane)
            val newState = state.updateStorage(Storage(plane))

            assertIllegalArgument("Cannot delete Plane 0, because it is used!") {
                REDUCER.invoke(newState, action)
            }
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
        fun `A language of a plane must exist `() {
            val plane = Plane(PLANE_ID_0, languages = setOf(UNKNOWN_LANGUAGE_ID))
            val action = UpdatePlane(plane)

            assertIllegalArgument("Requires unknown Language 99!") { REDUCER.invoke(state, action) }
        }

        @Nested
        inner class HeartPlaneTest {

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
                val plane1 = Plane(PLANE_ID_0, purpose = HeartPlane(GOD_ID_0))
                val newState = state.updateStorage(Storage(listOf(plane0, Plane(PLANE_ID_1))))
                val action = UpdatePlane(plane1)

                assertEquals(plane1, REDUCER.invoke(newState, action).first.getPlaneStorage().get(PLANE_ID_0))
            }
        }

        @Nested
        inner class PrisonPlaneTest {

            @Test
            fun `A prison plane requires a god`() {
                val plane = Plane(PLANE_ID_0, purpose = PrisonPlane(setOf(UNKNOWN_GOD_ID)))
                val action = UpdatePlane(plane)

                assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
            }

            @Test
            fun `A god can only have 1 prison plane`() {
                val plane0 = Plane(PLANE_ID_0, purpose = PrisonPlane(setOf(GOD_ID_0)))
                val plane1 = Plane(PLANE_ID_1, purpose = PrisonPlane(setOf(GOD_ID_0)))
                val newState = state.updateStorage(Storage(listOf(plane0, Plane(PLANE_ID_1))))
                val action = UpdatePlane(plane1)

                assertIllegalArgument("God 0 already has a prison plane!") { REDUCER.invoke(newState, action) }
            }

            @Test
            fun `Update a prison plane`() {
                val plane0 = Plane(PLANE_ID_0, purpose = PrisonPlane(setOf(GOD_ID_0)))
                val plane1 = Plane(PLANE_ID_0, purpose = PrisonPlane(setOf(GOD_ID_0)))
                val newState = state.updateStorage(Storage(listOf(plane0, Plane(PLANE_ID_1))))
                val action = UpdatePlane(plane1)

                assertEquals(plane1, REDUCER.invoke(newState, action).first.getPlaneStorage().get(PLANE_ID_0))
            }
        }

        @Test
        fun `Update is valid`() {
            val plane = Plane(PLANE_ID_0, NAME)
            val action = UpdatePlane(plane)

            assertEquals(plane, REDUCER.invoke(state, action).first.getPlaneStorage().get(PLANE_ID_0))
        }
    }

}