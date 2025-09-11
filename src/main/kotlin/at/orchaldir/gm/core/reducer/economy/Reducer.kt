package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.economy.canDeleteBusiness
import at.orchaldir.gm.core.selector.economy.canDeleteJob
import at.orchaldir.gm.core.selector.economy.money.canDeleteCurrency
import at.orchaldir.gm.core.selector.economy.money.canDeleteCurrencyUnit
import at.orchaldir.gm.utils.redux.Reducer

val ECONOMY_REDUCER: Reducer<EconomyAction, State> = { state, action ->
    when (action) {
        // business
        is CreateBusiness -> CREATE_BUSINESS(state, action)
        is DeleteBusiness -> deleteElement(state, action.id, State::canDeleteBusiness)
        is UpdateBusiness -> UPDATE_BUSINESS(state, action)
        // currency
        is CreateCurrency -> CREATE_CURRENCY(state, action)
        is DeleteCurrency -> deleteElement(state, action.id, State::canDeleteCurrency)
        is UpdateCurrency -> UPDATE_CURRENCY(state, action)
        // currency unit
        is CreateCurrencyUnit -> CREATE_CURRENCY_UNIT(state, action)
        is DeleteCurrencyUnit -> deleteElement(state, action.id, State::canDeleteCurrencyUnit)
        is UpdateCurrencyUnit -> UPDATE_CURRENCY_UNIT(state, action)
        // job
        is CreateJob -> CREATE_JOB(state, action)
        is DeleteJob -> deleteElement(state, action.id, State::canDeleteJob)
        is UpdateJob -> UPDATE_JOB(state, action)
    }
}
