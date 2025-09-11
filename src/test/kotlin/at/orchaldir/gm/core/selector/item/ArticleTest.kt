package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.ARTICLE_ID_0
import at.orchaldir.gm.ISSUE_ID_0
import at.orchaldir.gm.JOB_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.PeriodicalIssue
import at.orchaldir.gm.core.selector.item.periodical.canDeleteArticle
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArticleTest {

    @Nested
    inner class CanDeleteTest {
        private val article = Article(ARTICLE_ID_0)
        private val state = State(
            listOf(
                Storage(article),
            )
        )

        @Test
        fun `Cannot delete an article published in a periodical issue`() {
            val issue = PeriodicalIssue(ISSUE_ID_0, articles =setOf(ARTICLE_ID_0))
            val newState = state.updateStorage(Storage(issue))

            failCanDelete(newState, ISSUE_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(
                DeleteResult(ARTICLE_ID_0).addId(blockingId),
                state.canDeleteArticle(ARTICLE_ID_0)
            )
        }
    }

}