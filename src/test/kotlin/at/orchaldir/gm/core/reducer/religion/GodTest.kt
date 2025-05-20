package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteGod
import at.orchaldir.gm.core.action.UpdateGod
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.PersonalityTrait
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayOfGod
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.world.plane.HeartPlane
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PrisonPlane
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GodTest {

    private val god0 = God(GOD_ID_0)
    private val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(Domain(DOMAIN_ID_0)),
            Storage(god0),
            Storage(PersonalityTrait(PERSONALITY_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteGod(GOD_ID_0)

        @Test
        fun `Can delete an existing god`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getGodStorage().getSize())
        }

        @Test
        fun `Cannot delete the god of a heart plane`() {
            val plane = Plane(PLANE_ID_0, purpose = HeartPlane(GOD_ID_0))
            val newState = state.updateStorage(Storage(plane))

            assertIllegalArgument("Cannot delete God 0, because it is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete a god imprisoned in a plane`() {
            val plane = Plane(PLANE_ID_0, purpose = PrisonPlane(setOf(GOD_ID_0)))
            val newState = state.updateStorage(Storage(plane))

            assertIllegalArgument("Cannot delete God 0, because it is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete the god worshipped on a holiday`() {
            val plane = Holiday(HOLIDAY_ID_0, purpose = HolidayOfGod(GOD_ID_0))
            val newState = state.updateStorage(Storage(plane))

            assertIllegalArgument("Cannot delete God 0, because it is used!") { REDUCER.invoke(newState, action) }
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown God 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateGod(God(UNKNOWN_GOD_ID))

            assertIllegalArgument("Requires unknown God 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown domain`() {
            val action = UpdateGod(God(GOD_ID_0, domains = setOf(UNKNOWN_DOMAIN_ID)))

            assertIllegalArgument("Requires unknown Domain 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot use an unknown personality trait`() {
            val action = UpdateGod(God(GOD_ID_0, personality = setOf(UNKNOWN_PERSONALITY_ID)))

            assertIllegalArgument("Requires unknown Personality Trait 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Update a god`() {
            val god = God(
                GOD_ID_0,
                NAME,
                null,
                Gender.Genderless,
                setOf(PERSONALITY_ID_0),
                setOf(DOMAIN_ID_0),
            )
            val action = UpdateGod(god)

            assertEquals(god, REDUCER.invoke(state, action).first.getGodStorage().get(GOD_ID_0))
        }
    }

}