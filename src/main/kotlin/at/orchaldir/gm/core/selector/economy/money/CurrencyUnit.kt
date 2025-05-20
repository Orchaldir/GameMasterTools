package at.orchaldir.gm.core.selector.economy.money

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.*
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.selector.calculateWeight
import at.orchaldir.gm.utils.math.unit.WEIGHTLESS

fun State.canDeleteCurrencyUnit(id: CurrencyUnitId) = true

fun State.countCurrencyUnits(currency: CurrencyId) = getCurrencyUnitStorage()
    .getAll()
    .count { it.currency == currency }

fun State.countCurrencyUnits(font: FontId) = getCurrencyUnitStorage()
    .getAll()
    .count { it.format.getFonts().contains(font) }

fun State.countCurrencyUnits(material: MaterialId) = getCurrencyUnitStorage()
    .getAll()
    .count { it.format.contains(material) }

fun State.getCurrencyUnits(currency: CurrencyId) = getCurrencyUnitStorage()
    .getAll()
    .filter { it.currency == currency }

fun State.getCurrencyUnits(font: FontId) = getCurrencyUnitStorage()
    .getAll()
    .filter { it.format.getFonts().contains(font) }

fun State.getCurrencyUnits(material: MaterialId) = getCurrencyUnitStorage()
    .getAll()
    .filter { it.format.contains(material) }

fun State.calculateWeight(unit: CurrencyUnit) = when (val format = unit.format) {
    UndefinedCurrencyFormat -> WEIGHTLESS
    is Coin -> calculateWeight(format.material, format.shape.calculateVolume(format.radius, format.thickness))
    is HoledCoin -> {
        val outerVolume = format.shape.calculateVolume(format.radius, format.thickness)
        val holeVolume = format.holeShape.calculateVolume(format.calculateHoleRadius(), format.thickness)

        calculateWeight(format.material, outerVolume - holeVolume)
    }

    is BiMetallicCoin -> {
        val outerVolume = format.shape.calculateVolume(format.radius, format.thickness)
        val innerVolume = format.innerShape.calculateVolume(format.calculateInnerRadius(), format.thickness)

        calculateWeight(format.material, outerVolume - innerVolume) + calculateWeight(format.innerMaterial, innerVolume)
    }
}

