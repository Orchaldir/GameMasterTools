package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.checkFactor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.checkDistance

fun validateFormat(
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
