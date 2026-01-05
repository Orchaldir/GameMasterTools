package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.InPlane
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.plane.Demiplane
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.ReflectivePlane
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlaneTest {

    @Nested
    inner class CanDeleteTest {
        private val plane = Plane(PLANE_ID_0)
        private val state = State(
            listOf(
                Storage(plane),
            )
        )
        private val position = InPlane(PLANE_ID_0)

        @Test
        fun `Cannot delete the plane linked to a moon`() {
            val newState = state.updateStorage(Moon(MOON_ID_0, plane = PLANE_ID_0))

            failCanDelete(newState, MOON_ID_0)
        }

        @Test
        fun `Cannot delete the plane linked to a demiplane`() {
            val plane1 = Plane(PLANE_ID_1, purpose = Demiplane(PLANE_ID_0))
            val newState = state.updateStorage(Storage(listOf(plane, plane1)))

            failCanDelete(newState, PLANE_ID_1)
        }

        @Test
        fun `Cannot delete a reflected plane`() {
            val plane1 = Plane(PLANE_ID_1, purpose = ReflectivePlane(PLANE_ID_0))
            val newState = state.updateStorage(Storage(listOf(plane, plane1)))

            failCanDelete(newState, PLANE_ID_1)
        }

        @Test
        fun `Cannot delete an element used as home`() {
            val housingStatus = History<Position>(position)
            val character = Character(CHARACTER_ID_0, housingStatus = housingStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        @Test
        fun `Cannot delete an element used as a position`() {
            val business = Business(BUSINESS_ID_0, position = position)
            val newState = state.updateStorage(business)

            failCanDelete(newState, BUSINESS_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(PLANE_ID_0).addId(blockingId), state.canDeletePlane(PLANE_ID_0))
        }
    }

}