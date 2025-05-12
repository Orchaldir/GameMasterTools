package at.orchaldir.gm.core.selector.quote

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.quote.QuoteId

fun State.canDeleteQuote(race: QuoteId) = false
