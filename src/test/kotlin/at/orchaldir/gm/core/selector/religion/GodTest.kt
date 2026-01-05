package at.orchaldir.gm.core.selector.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfGod
import at.orchaldir.gm.core.model.util.BeliefStatus
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.MaskOfOtherGod
import at.orchaldir.gm.core.model.util.WorshipOfGod
import at.orchaldir.gm.core.model.world.plane.HeartPlane
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PrisonPlane
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GodTest {

    @Nested
    inner class CanDeleteTest {
        private val god = God(GOD_ID_0)
        private val state = State(
            listOf(
                Storage(god),
            )
        )

        @Test
        fun `Cannot delete the god of a heart plane`() {
            val plane = Plane(PLANE_ID_0, purpose = HeartPlane(GOD_ID_0))
            val newState = state.updateStorage(plane)

            failCanDelete(newState, PLANE_ID_0)
        }

        @Test
        fun `Cannot delete a god imprisoned in a plane`() {
            val plane = Plane(PLANE_ID_0, purpose = PrisonPlane(setOf(GOD_ID_0)))
            val newState = state.updateStorage(plane)

            failCanDelete(newState, PLANE_ID_0)
        }

        @Test
        fun `Cannot delete the god worshipped on a holiday`() {
            val plane = Holiday(HOLIDAY_ID_0, purpose = HolidayOfGod(GOD_ID_0))
            val newState = state.updateStorage(plane)

            failCanDelete(newState, HOLIDAY_ID_0)
        }

        @Test
        fun `Cannot delete a god that has a mask`() {
            val mask = God(GOD_ID_1, authenticity = MaskOfOtherGod(GOD_ID_0))
            val newState = state.updateStorage(Storage(listOf(god, mask)))

            failCanDelete(newState, GOD_ID_1)
        }

        @Test
        fun `Cannot delete a god that is part of a pantheon`() {
            val pantheon = Pantheon(PANTHEON_ID_0, gods = setOf(GOD_ID_0))
            val newState = state.updateStorage(pantheon)

            failCanDelete(newState, PANTHEON_ID_0)
        }

        @Test
        fun `Cannot delete a god that a character believes in`() {
            val beliefStatus = History<BeliefStatus>(WorshipOfGod(GOD_ID_0))
            val character = Character(CHARACTER_ID_0, beliefStatus = beliefStatus)
            val newState = state.updateStorage(character)

            failCanDelete(newState, CHARACTER_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(GOD_ID_0).addId(blockingId), state.canDeleteGod(GOD_ID_0))
        }
    }

}