package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateCurrency
import at.orchaldir.gm.core.action.DeleteCurrency
import at.orchaldir.gm.core.action.UpdateCurrency
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Currency
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CURRENCY: Reducer<CreateCurrency, State> = { state, _ ->
    val currency = Currency(state.getCurrencyStorage().nextId)

    noFollowUps(state.updateStorage(state.getCurrencyStorage().add(currency)))
}

val DELETE_CURRENCY: Reducer<DeleteCurrency, State> = { state, action ->
    state.getCurrencyStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getCurrencyStorage().remove(action.id)))
}

val UPDATE_CURRENCY: Reducer<UpdateCurrency, State> = { state, action ->
    val currency = action.currency
    state.getCurrencyStorage().require(currency.id)

    noFollowUps(state.updateStorage(state.getCurrencyStorage().update(currency)))
}
