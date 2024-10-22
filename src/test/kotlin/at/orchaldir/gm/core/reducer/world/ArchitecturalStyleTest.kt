package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteArchitecturalStyle
import at.orchaldir.gm.core.action.UpdateArchitecturalStyle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.MonthDefinition
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val ID0 = ArchitecturalStyleId(0)
private val ID1 = ArchitecturalStyleId(1)
private val CALENDAR0 = Calendar(CalendarId(0), months = listOf(MonthDefinition("a")))

class ArchitecturalStyleTest {

    @Nested
    inner class DeleteTest {

        @Test
        fun `Can delete an unused style`() {
            val state = State(Storage(ArchitecturalStyle(ID0)))
            val action = DeleteArchitecturalStyle(ID0)

            assertEquals(0, REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown style`() {
            val action = DeleteArchitecturalStyle(ID0)

            assertIllegalArgument("Requires unknown Architectural Style 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot delete used style`() {
            val state = State(
                listOf(
                    Storage(ArchitecturalStyle(ID0)),
                    Storage(Building(BuildingId(0), architecturalStyle = ID0))
                )
            )
            val action = DeleteArchitecturalStyle(ID0)

            assertIllegalArgument("Architectural Style 0 is used!") { REDUCER.invoke(state, action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ID0))

            assertIllegalArgument("Requires unknown Architectural Style 0!") { REDUCER.invoke(State(), action) }
        }

        @Test
        fun `Cannot revive an unknown style`() {
            val state = State(Storage(ArchitecturalStyle(ID0)))
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ID0, revival = ID1))

            assertIllegalArgument("Cannot revive unknown architectural style 1!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot start and end in the same year`() {
            val state = State(Storage(ArchitecturalStyle(ID0)))
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ID0, start = Year(0), end = Year(0)))

            assertIllegalArgument("Architectural style must end after it started!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Cannot end before it started`() {
            val state = State(Storage(ArchitecturalStyle(ID0)))
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ID0, start = Year(1), end = Year(0)))

            assertIllegalArgument("Architectural style must end after it started!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Style ends after it started`() {
            val state = State(Storage(ArchitecturalStyle(ID0)))
            val style = ArchitecturalStyle(ID0, start = Year(2), end = Year(3))
            val action = UpdateArchitecturalStyle(style)

            assertEquals(style, REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().get(ID0))
        }

        @Test
        fun `Style cannot start after a building using it was build`() {
            val state = State(
                listOf(
                    Storage(ArchitecturalStyle(ID0)),
                    Storage(Building(BuildingId(0))),
                    Storage(CALENDAR0),
                )
            )
            val action = UpdateArchitecturalStyle(ArchitecturalStyle(ID0, start = Year(1)))

            assertIllegalArgument("Architectural Style 0 didn't exist yet, when building 0 was build!") {
                REDUCER.invoke(state, action)
            }
        }

        @Test
        fun `Update is valid`() {
            val state = State(Storage(ArchitecturalStyle(ID0)))
            val style = ArchitecturalStyle(ID0, "Test")
            val action = UpdateArchitecturalStyle(style)

            assertEquals(style, REDUCER.invoke(state, action).first.getArchitecturalStyleStorage().get(ID0))
        }
    }

}