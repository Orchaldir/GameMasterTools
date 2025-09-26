package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.UpdateCurrencyUnit
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.checkFactor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.checkDistance
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

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
    validateFormat(unit.format)
}

private fun validateFormat(
    format: CurrencyFormat,
) = when (format) {
    UndefinedCurrencyFormat -> doNothing()
    is Coin -> {
        checkRadius(format.radius)
        checkThickness(format.thickness)
        checkRimFactor(format.rimFactor)
    }

    is HoledCoin -> {
        checkRadius(format.radius)
        checkThickness(format.thickness)
        checkRadiusFactor(format.holeFactor, "hole")
        checkRimFactor(format.rimFactor)
    }

    is BiMetallicCoin -> {
        require(format.material != format.innerMaterial) { "Outer & inner material are the same!" }
        checkRadius(format.radius)
        checkThickness(format.thickness)
        checkRimFactor(format.rimFactor)
        checkRadiusFactor(format.innerFactor, "inner")
    }
}

private fun checkRadius(radius: Distance) =
    checkDistance(radius, "radius", MIN_RADIUS, MAX_RADIUS)

private fun checkThickness(thickness: Distance) =
    checkDistance(thickness, "thickness", MIN_THICKNESS, MAX_THICKNESS)

private fun checkRimFactor(factor: Factor) =
    checkFactor(factor, "rim", ZERO, MAX_RIM_FACTOR)

private fun checkRadiusFactor(factor: Factor, label: String) =
    checkFactor(factor, label, MIN_RADIUS_FACTOR, MAX_RADIUS_FACTOR)
