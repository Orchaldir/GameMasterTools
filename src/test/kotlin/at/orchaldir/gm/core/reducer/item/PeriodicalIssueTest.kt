package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePeriodicalIssue
import at.orchaldir.gm.core.action.UpdatePeriodicalIssue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class PeriodicalIssueTest {

    val issue0 = PeriodicalIssue(ISSUE_ID_0, PERIODICAL_ID_0)
    val state = State(
        listOf(
            Storage(CALENDAR0),
            Storage(listOf(Periodical(PERIODICAL_ID_0, date = YEAR1), Periodical(PERIODICAL_ID_1))),
            Storage(issue0),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeletePeriodicalIssue(ISSUE_ID_0)

        @Test
        fun `Can delete an existing issue`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getPeriodicalIssueStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeletePeriodicalIssue(UNKNOWN_ISSUE_ID)

            assertIllegalArgument("Requires unknown Periodical Issue 99!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdatePeriodicalIssue(PeriodicalIssue(UNKNOWN_ISSUE_ID))

            assertIllegalArgument("Requires unknown Periodical Issue 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `The periodical is unknown`() {
            val action = UpdatePeriodicalIssue(PeriodicalIssue(ISSUE_ID_0, UNKNOWN_PERIODICAL_ID))

            assertIllegalArgument("Requires unknown Periodical 99!") { REDUCER.invoke(state, action) }
        }


        @Nested
        inner class DateTest {

            @Test
            fun `Issue cannot be published before the start of the periodical`() {
                val issue = PeriodicalIssue(ISSUE_ID_0, PERIODICAL_ID_0, YEAR0)
                val action = UpdatePeriodicalIssue(issue)

                assertIllegalArgument("The Issue 0 cannot be published before the start of the periodical!") {
                    REDUCER.invoke(state, action)
                }
            }

            @Test
            fun `The start date of the periodical is valid`() {
                val issue = PeriodicalIssue(ISSUE_ID_0, PERIODICAL_ID_0, YEAR1)

                assertUpdate(state, issue)
            }

            @Test
            fun `Any date after the start of periodical is valid`() {
                val issue = PeriodicalIssue(ISSUE_ID_0, PERIODICAL_ID_0, YEAR2)

                assertUpdate(state, issue)
            }
        }

        @Test
        fun `Test Success`() {
            val issue = PeriodicalIssue(ISSUE_ID_0)

            assertUpdate(state, issue)
        }

        private fun assertUpdate(
            oldState: State,
            issue: PeriodicalIssue,
        ) {
            val action = UpdatePeriodicalIssue(issue)

            val newState = REDUCER.invoke(oldState, action).first

            assertEquals(issue, newState.getPeriodicalIssueStorage().get(issue.id))
        }
    }

}