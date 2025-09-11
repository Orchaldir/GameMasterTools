package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.ARTICLE_ID_0
import at.orchaldir.gm.ISSUE_ID_0
import at.orchaldir.gm.PERIODICAL_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Periodical
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.selector.item.periodical.canDeleteArticle
import at.orchaldir.gm.core.selector.item.periodical.canDeletePeriodical
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PeriodicalTest {

    @Nested
    inner class CanDeleteTest {
        private val periodical = Periodical(PERIODICAL_ID_0)
        private val state = State(
            listOf(
                Storage(periodical),
            )
        )

        @Test
        fun `Cannot delete an periodical that published a periodical issue`() {
            val issue = PeriodicalIssue(ISSUE_ID_0, periodical = PERIODICAL_ID_0)
            val newState = state.updateStorage(Storage(issue))

            failCanDelete(newState, ISSUE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(PERIODICAL_ID_0).addId(blockingId),
                state.canDeletePeriodical(PERIODICAL_ID_0)
            )
        }
    }

}