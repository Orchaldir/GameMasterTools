package at.orchaldir.gm.core.reducer.util.quote

import at.orchaldir.gm.core.action.UpdateQuote
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_QUOTE: Reducer<UpdateQuote, State> = { state, action ->
    state.getQuoteStorage().require(action.quote.id)
    val quote = action.quote

    validateQuote(state, quote)

    noFollowUps(state.updateStorage(state.getQuoteStorage().update(action.quote)))
}

fun validateQuote(
    state: State,
    quote: Quote,
) {

}
