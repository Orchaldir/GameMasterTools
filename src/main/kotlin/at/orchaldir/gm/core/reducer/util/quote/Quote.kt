package at.orchaldir.gm.core.reducer.util.quote

import at.orchaldir.gm.core.action.CreateQuote
import at.orchaldir.gm.core.action.DeleteQuote
import at.orchaldir.gm.core.action.UpdateQuote
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quote.Quote
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.util.canDeleteQuote
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_QUOTE: Reducer<CreateQuote, State> = { state, _ ->
    val quote = Quote(state.getQuoteStorage().nextId)

    noFollowUps(state.updateStorage(state.getQuoteStorage().add(quote)))
}

val DELETE_QUOTE: Reducer<DeleteQuote, State> = { state, action ->
    state.getQuoteStorage().require(action.id)
    validateCanDelete(state.canDeleteQuote(action.id), action.id)

    noFollowUps(state.updateStorage(state.getQuoteStorage().remove(action.id)))
}

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
