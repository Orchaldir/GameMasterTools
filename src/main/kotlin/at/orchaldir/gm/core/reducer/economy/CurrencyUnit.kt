package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateCurrencyUnit
import at.orchaldir.gm.core.action.DeleteCurrencyUnit
import at.orchaldir.gm.core.action.UpdateCurrencyUnit
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CURRENCY_UNIT: Reducer<CreateCurrencyUnit, State> = { state, _ ->
    val currency = CurrencyUnit(state.getCurrencyUnitStorage().nextId)

    noFollowUps(state.updateStorage(state.getCurrencyUnitStorage().add(currency)))
}

val DELETE_CURRENCY_UNIT: Reducer<DeleteCurrencyUnit, State> = { state, action ->
    state.getCurrencyUnitStorage().require(action.id)

    noFollowUps(state.updateStorage(state.getCurrencyUnitStorage().remove(action.id)))
}

val UPDATE_CURRENCY_UNIT: Reducer<UpdateCurrencyUnit, State> = { state, action ->
    val unit = action.unit
    state.getCurrencyUnitStorage().require(unit.id)
    validateCurrencyUnit(state, unit)

    noFollowUps(state.updateStorage(state.getCurrencyUnitStorage().update(unit)))
}

fun validateCurrencyUnit(
    state: State,
    unit: CurrencyUnit,
) {
    val currency = state.getCurrencyStorage().getOrThrow(unit.currency)
    currency.getDenomination(unit.denomination)
    unit.format.getMaterials().forEach { state.getMaterialStorage().require(it) }
}
