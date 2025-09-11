package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.selector.item.getTextsContaining
import at.orchaldir.gm.core.selector.item.periodical.getArticlesContaining

fun State.canDeleteQuote(id: QuoteId) = DeleteResult(id)
    .addElements(getArticlesContaining(id))
    .addElements(getTextsContaining(id))
