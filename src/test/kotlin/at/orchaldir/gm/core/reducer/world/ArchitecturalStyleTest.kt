package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArchitecturalStyleTest {

    private val style0 = ArchitecturalStyle(ARCHITECTURAL_ID_0)
    private val state = State(
        listOf(
            Storage(style0),
            Storage(CALENDAR0),
        )
    )

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateAction(ArchitecturalStyle(ARCHITECTURAL_ID_0))

            assertIllegalArgument("Requires unknown Architectural Style 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot revive an unknown style`() {
            val action = UpdateAction(ArchitecturalStyle(ARCHITECTURAL_ID_0, revival = UNKNOWN_ARCHITECTURAL_ID))

            assertIllegalArgument("Cannot revive unknown Architectural Style 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot end before it started`() {
            val action = UpdateAction(ArchitecturalStyle(ARCHITECTURAL_ID_0, start = YEAR1, end = YEAR0))

            assertIllegalArgument("The Architectural Style 0 must end after it started!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Style ends after it started`() {
            val style = ArchitecturalStyle(ARCHITECTURAL_ID_0, start = YEAR0, end = YEAR1)
            val action = UpdateAction(style)

            assertEquals(
                style,
                REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().get(ARCHITECTURAL_ID_0)
            )
        }

        @Test
        fun `Style cannot start after a building using it was build`() {
            val building = Building(BuildingId(0), constructionDate = YEAR0, style = ARCHITECTURAL_ID_0)
            val newState = state.updateStorage(Storage(building))
            val action = UpdateAction(ArchitecturalStyle(ARCHITECTURAL_ID_0, start = YEAR1))

            assertIllegalArgument("Architectural Style 0 didn't exist yet, when building 0 was build!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Start date is in the future`() {
            val action = UpdateAction(ArchitecturalStyle(ARCHITECTURAL_ID_0, start = FUTURE_YEAR_0))

            assertIllegalArgument("Date (Architectural Style's Start) is in the future!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `End date is in the future`() {
            val action = UpdateAction(ArchitecturalStyle(ARCHITECTURAL_ID_0, end = FUTURE_YEAR_0))

            assertIllegalArgument("Date (Architectural Style's End) is in the future!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val style = ArchitecturalStyle(ARCHITECTURAL_ID_0, NAME)
            val action = UpdateAction(style)

            assertEquals(
                style,
                REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().get(ARCHITECTURAL_ID_0)
            )
        }
    }

}