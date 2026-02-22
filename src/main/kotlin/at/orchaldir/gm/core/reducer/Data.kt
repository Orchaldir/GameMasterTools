package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Config
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.EconomyConfig
import at.orchaldir.gm.core.model.rpg.EquipmentConfig
import at.orchaldir.gm.core.model.rpg.RpgConfig
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.selector.economy.getRequiredStandards
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_DATA: Reducer<UpdateData, State> = { state, action ->
    validateData(state, action.config)

    noFollowUps(state.copy(config = action.config))
}

fun validateData(state: State, config: Config) {
    state.getCalendarStorage().require(config.time.defaultCalendar)
    validateEconomy(state, config.economy)
    validateRpg(config.rpg)
}

private fun validateEconomy(state: State, economy: EconomyConfig) {
    state.getCurrencyStorage().require(economy.defaultCurrency)
    val requiredStandards = state.getRequiredStandards()

    if (requiredStandards != null) {
        require(economy.standardsOfLiving.size >= requiredStandards) {
            "The number of required Standards of Living is $requiredStandards!"
        }
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

private fun validateRpg(data: RpgConfig) {
    validateEquipment(data.equipment)
    data.damage.validate()
}

private fun validateEquipment(data: EquipmentConfig) {
    data.damageModifier.validate()
    require(data.maxDamageResistance > 0) { "Max Damage Resistance must be greater than 0!" }
    data.damageResistanceModifier.validate()
    require(data.maxDefenseBonus > 0) { "Max Defense Bonus must be greater than 0!" }
    data.defenseBonusModifier.validate()
}