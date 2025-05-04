package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.selector.economy.Economy
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_DATA: Reducer<UpdateData, State> = { state, action ->
    validateData(state, action.data)

    noFollowUps(state.copy(data = action.data))
}

fun validateData(state: State, data: Data) {
    state.getCalendarStorage().require(data.time.defaultCalendar)
    validateEconomy(state, data.economy)
}

private fun validateEconomy(state: State, economy: Economy) {
    state.getCurrencyStorage().require(economy.defaultCurrency)

    val usedNames = mutableSetOf<Name>()

    economy.standardsOfLiving.forEach { standard ->
        require(!usedNames.contains(standard.name)) {
            "Name '${standard.name.text}' is duplicated for standards of living!"
        }
        usedNames.add(standard.name)
    }
}