package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.ARTICLE_ID_0
import at.orchaldir.gm.QUOTE_ID_0
import at.orchaldir.gm.TEXT_ID_0
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.FullArticleContent
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.content.LinkedQuote
import at.orchaldir.gm.core.model.item.text.content.SimpleChapter
import at.orchaldir.gm.core.model.item.text.content.SimpleChapters
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class QuoteTest {

    @Nested
    inner class CanDeleteTest {
        private val quote = Quote(QUOTE_ID_0)
        private val state = State(
            listOf(
                Storage(quote),
            )
        )

        @Test
        fun `Cannot delete a quote used by an article`() {
            val content = FullArticleContent(listOf(LinkedQuote(QUOTE_ID_0)))
            val article = Article(ARTICLE_ID_0, content = content)
            val newState = state.updateStorage(article)

            failCanDelete(newState, ARTICLE_ID_0)
        }

        @Test
        fun `Cannot delete a quote used by an text`() {
            val chapter = SimpleChapter(0, listOf(LinkedQuote(QUOTE_ID_0)))
            val content = SimpleChapters(listOf(chapter))
            val text = Text(TEXT_ID_0, content = content)
            val newState = state.updateStorage(text)

            failCanDelete(newState, TEXT_ID_0)
        }

        private fun <ID : Id<ID>> failCanDelete(state: State, blockingId: ID) {
            assertEquals(DeleteResult(QUOTE_ID_0).addId(blockingId), state.canDeleteQuote(QUOTE_ID_0))
        }
    }

}