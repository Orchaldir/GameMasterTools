package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.QUOTE_ID_0
import at.orchaldir.gm.UNKNOWN_QUOTE_ID
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.action.UpdateQuote
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.info.quote.Quote
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class QuoteTest {
    val state = State(Storage(Quote(QUOTE_ID_0)))

    @Nested
    inner class UpdateTest {

        @Test
        fun `Cannot update unknown id`() {
            val action = UpdateQuote(Quote(UNKNOWN_QUOTE_ID))

            assertIllegalArgument("Requires unknown Quote 99!") { REDUCER.invoke(state, action) }
        }

        @Test
        fun `Quote exists`() {
            val quote = Quote(QUOTE_ID_0, NotEmptyString.init("Test"))
            val action = UpdateQuote(quote)

            assertEquals(quote, REDUCER.invoke(state, action).first.getQuoteStorage().get(QUOTE_ID_0))
        }
    }

}