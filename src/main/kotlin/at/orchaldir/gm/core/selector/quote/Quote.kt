package at.orchaldir.gm.core.selector.quote

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.periodical.countArticles

fun State.canDeleteQuote(quote: QuoteId) = countArticles(quote) == 0 &&
        countTexts(quote) == 0
