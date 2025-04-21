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

    val issue0 = PeriodicalIssue(ISSUE_ID_0, PERIODICAL_ID_0, 0)
    val state = State(
        listOf(
            Storage(listOf(Periodical(PERIODICAL_ID_0), Periodical(PERIODICAL_ID_1))),
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
        inner class IssueNumberTest {

            @Test
            fun `Must not be negative`() {
                assertIllegalArgument("Invalid issue number -1!") {
                    PeriodicalIssue(ISSUE_ID_0, number = -1)
                }
            }

            @Test
            fun `Already used by the same periodical`() {
                val issue = PeriodicalIssue(ISSUE_ID_1, PERIODICAL_ID_0, 0)
                val action = UpdatePeriodicalIssue(issue)
                val newState = state.updateStorage(
                    Storage(listOf(issue0, PeriodicalIssue(ISSUE_ID_1, PERIODICAL_ID_0, 1)))
                )

                assertIllegalArgument("The issue number 0 is already used by the periodical!") {
                    REDUCER.invoke(newState, action)
                }
            }

            @Test
            fun `Can be duplicated for different periodicals`() {
                val issue = PeriodicalIssue(ISSUE_ID_1, PERIODICAL_ID_1, 0)
                val action = UpdatePeriodicalIssue(issue)
                val newState = state.updateStorage(
                    Storage(listOf(issue0, PeriodicalIssue(ISSUE_ID_1, PERIODICAL_ID_1, 1)))
                )

                assertEquals(
                    issue,
                    REDUCER.invoke(newState, action).first.getPeriodicalIssueStorage().get(ISSUE_ID_1)
                )
            }

            @Test
            fun `Same issue can reuse its own number`() {
                val action = UpdatePeriodicalIssue(issue0)

                assertEquals(
                    issue0,
                    REDUCER.invoke(state, action).first.getPeriodicalIssueStorage().get(ISSUE_ID_0)
                )
            }
        }

        @Test
        fun `Test Success`() {
            val issue = PeriodicalIssue(ISSUE_ID_0, number = 2)
            val action = UpdatePeriodicalIssue(issue)

            assertEquals(
                issue,
                REDUCER.invoke(state, action).first.getPeriodicalIssueStorage().get(ISSUE_ID_0)
            )
        }
    }

}