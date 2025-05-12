package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.ARTICLE_ID_0
import at.orchaldir.gm.UNKNOWN_ARTICLE_ID
import at.orchaldir.gm.UNKNOWN_QUOTE_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.DeleteArticle
import at.orchaldir.gm.core.action.UpdateArticle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.FullArticleContent
import at.orchaldir.gm.core.model.item.text.content.LinkedQuote
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class ArticleTest {

    private val STATE = State(
        listOf(
            Storage(listOf(Article(ARTICLE_ID_0))),
        )
    )

    @Nested
    inner class DeleteTest {
        val action = DeleteArticle(ARTICLE_ID_0)

        @Test
        fun `Can delete an existing article`() {
            assertFalse(REDUCER.invoke(STATE, action).first.getArticleStorage().contains(ARTICLE_ID_0))
        }

        @Test
        fun `Cannot delete unknown id`() {
            assertIllegalArgument("Requires unknown Article 0!") { REDUCER.invoke(State(), action) }
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateArticle(Article(UNKNOWN_ARTICLE_ID))

            assertIllegalArgument("Requires unknown Article 99!") { REDUCER.invoke(STATE, action) }
        }

        @Test
        fun `Unknown quote`() {
            val entry = LinkedQuote(UNKNOWN_QUOTE_ID)
            val content = FullArticleContent(listOf(entry))
            val action = UpdateArticle(Article(ARTICLE_ID_0, content = content))

            assertIllegalArgument("Requires unknown Quote 99!") { REDUCER.invoke(STATE, action) }
        }
    }

}