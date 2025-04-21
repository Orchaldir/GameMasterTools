package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.DeletePeriodicalIssue
import at.orchaldir.gm.core.action.UpdatePeriodicalIssue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class PeriodicalIssueTest {

    val state = State(
        listOf(
            Storage(Periodical(PERIODICAL_ID_0)),
            Storage(PeriodicalIssue(PERIODICAL_ISSUE_ID_0, PERIODICAL_ID_0)),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeletePeriodicalIssue(PERIODICAL_ISSUE_ID_0)

        @Test
        fun `Can delete an existing issue`() {
            assertEquals(0, REDUCER.invoke(state, action).first.getPeriodicalIssueStorage().getSize())
        }

        @Test
        fun `Cannot delete unknown id`() {
            val action = DeletePeriodicalIssue(UNKNOWN_PERIODICAL_ISSUE_ID)

            assertIllegalArgument("Requires unknown Periodical Issue 99!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdatePeriodicalIssue(PeriodicalIssue(UNKNOWN_PERIODICAL_ISSUE_ID))

            assertFailsWith<IllegalArgumentException> { REDUCER.invoke(state, action) }
        }

        @Test
        fun `The periodical is unknown`() {
            val action = UpdatePeriodicalIssue(PeriodicalIssue(PERIODICAL_ISSUE_ID_0, UNKNOWN_PERIODICAL_ID))

            assertIllegalArgument("Requires unknown Periodical 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `The issue number must not be negative`() {
            assertIllegalArgument("Invalid issue number -1!") {
                PeriodicalIssue(PERIODICAL_ISSUE_ID_0, number = -1)
            }
        }

        @Test
        fun `Test Success`() {
            val issue = PeriodicalIssue(PERIODICAL_ISSUE_ID_0, number = 2)
            val action = UpdatePeriodicalIssue(issue)

            assertEquals(
                issue,
                REDUCER.invoke(state, action).first.getPeriodicalIssueStorage().get(PERIODICAL_ISSUE_ID_0)
            )
        }
    }

}