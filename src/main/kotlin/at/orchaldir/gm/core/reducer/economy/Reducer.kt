package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val ECONOMY_REDUCER: Reducer<EconomyAction, State> = { state, action ->
    when (action) {
        // business
        is CreateBusiness -> CREATE_BUSINESS(state, action)
        is DeleteBusiness -> DELETE_BUSINESS(state, action)
        is UpdateBusiness -> UPDATE_BUSINESS(state, action)
        // currency
        is CreateCurrency -> CREATE_CURRENCY(state, action)
        is DeleteCurrency -> DELETE_CURRENCY(state, action)
        is UpdateCurrency -> UPDATE_CURRENCY(state, action)
        // currency unit
        is CreateCurrencyUnit -> CREATE_CURRENCY_UNIT(state, action)
        is DeleteCurrencyUnit -> DELETE_CURRENCY_UNIT(state, action)
        is UpdateCurrencyUnit -> UPDATE_CURRENCY_UNIT(state, action)
        // job
        is CreateJob -> CREATE_JOB(state, action)
        is DeleteJob -> DELETE_JOB(state, action)
        is UpdateJob -> UPDATE_JOB(state, action)
    }
}
