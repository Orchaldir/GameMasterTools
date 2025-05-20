package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.selector.economy.getRequiredStandards
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
    val requiredStandards = state.getRequiredStandards()

    require(economy.standardsOfLiving.size >= requiredStandards) {
        "The number of required Standards of Living is $requiredStandards!"
    }

    val usedNames = mutableSetOf<Name>()
    var lastIncome = -1

    economy.standardsOfLiving.forEach { standard ->
        require(!usedNames.contains(standard.name)) {
            "Name '${standard.name.text}' is duplicated for standards of living!"
        }
        require(standard.maxYearlyIncome.value > lastIncome) {
            "Standard of Living '${standard.name.text}' must have a greater income than the last one!"
        }

        usedNames.add(standard.name)
        lastIncome = standard.maxYearlyIncome.value
    }
}