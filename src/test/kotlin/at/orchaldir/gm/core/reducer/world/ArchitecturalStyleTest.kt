package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArchitecturalStyleTest {

    private val style0 = ArchitecturalStyle(ARCHITECTURAL_ID0)
    private val state = State(
        listOf(
            Storage(style0),
            Storage(CALENDAR0),
        )
    )

    @Nested
    inner class DeleteTest {

        private val revivalStyle = ArchitecturalStyle(ARCHITECTURAL_ID1, revival = ARCHITECTURAL_ID0)
        private val revivalState = state.updateStorage(Storage(listOf(style0, revivalStyle)))
        private val action = DeleteArchitecturalStyle(ARCHITECTURAL_ID0)

        @Test
        fun `Can delete an unused style`() {

            assertEquals(0, REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown style`() {
            val action = DeleteArchitecturalStyle(ARCHITECTURAL_ID0)

            assertIllegalArgument("Requires unknown Architectural Style 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete a style used by building`() {
            val newState = state.updateStorage(Storage(Building(BuildingId(0), style = ARCHITECTURAL_ID0)))

            assertIllegalArgument("Cannot delete Architectural Style 0, because it is used!") {
                REDUCER.invoke(
                    newState,
                    action
                )
            }
        }

        @Test
        fun `Cannot delete a revived style`() {
            assertIllegalArgument("Cannot delete Architectural Style 0, because it is used!") {
                REDUCER.invoke(
                    revivalState,
                    action
                )
            }
        }

        @Test
        fun `Can delete a revival style`() {
            val action = DeleteArchitecturalStyle(ARCHITECTURAL_ID1)

            assertEquals(
                listOf(style0),
                REDUCER.invoke(revivalState, action).first.getArchitecturalStyleStorage().getAll().toList()
            )
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0))

            assertIllegalArgument("Requires unknown Architectural Style 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot revive an unknown style`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0, revival = ARCHITECTURAL_ID1))

            assertIllegalArgument("Cannot revive unknown architectural style 1!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot start and end in the same year`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0, start = Year(0), end = Year(0)))

            assertIllegalArgument("Architectural style must end after it started!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot end before it started`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0, start = YEAR1, end = YEAR0))

            assertIllegalArgument("Architectural style must end after it started!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Style ends after it started`() {
            val style = ArchitecturalStyle(ARCHITECTURAL_ID0, start = YEAR0, end = YEAR1)
            val action = UpdateArchitecturalStyle(style)

            assertEquals(
                style,
                REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().get(ARCHITECTURAL_ID0)
            )
        }

        @Test
        fun `Style cannot start after a building using it was build`() {
            val building = Building(BuildingId(0), constructionDate = YEAR0, style = ARCHITECTURAL_ID0)
            val newState = state.updateStorage(Storage(building))
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0, start = YEAR1))

            assertIllegalArgument("Architectural Style 0 didn't exist yet, when building 0 was build!") {
                REDUCER.invoke(newState, action)
            }
        }

        @Test
        fun `Start date is in the future`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0, start = FUTURE_YEAR_0))

            assertIllegalArgument("Date (Architectural Style's Start) is in the future!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `End date is in the future`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ARCHITECTURAL_ID0, end = FUTURE_YEAR_0))

            assertIllegalArgument("Date (Architectural Style's End) is in the future!") {
                REDUCER.invoke(
                    state,
                    action
                )
            }
        }

        @Test
        fun `Update is valid`() {
            val style = ArchitecturalStyle(ARCHITECTURAL_ID0, NAME)
            val action = UpdateArchitecturalStyle(style)

            assertEquals(
                style,
                REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().get(ARCHITECTURAL_ID0)
            )
        }
    }

}