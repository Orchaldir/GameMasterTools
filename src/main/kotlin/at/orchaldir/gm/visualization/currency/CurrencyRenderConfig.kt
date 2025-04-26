package at.orchaldir.gm.visualization.currency

import at.orchaldir.gm.core.model.economy.money.Coin
import at.orchaldir.gm.core.model.economy.money.CurrencyFormat
import at.orchaldir.gm.core.model.economy.money.HoledCoin
import at.orchaldir.gm.core.model.economy.money.UndefinedCurrencyFormat
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.renderer.model.LineOptions

data class CurrencyRenderConfig(
    val line: LineOptions,
    val padding: Factor,
) {

    fun calculatePaddedSize(format: CurrencyFormat) = calculateSize(format) * padding

    fun calculatePaddedCoinSize(radius: Distance) = calculateCoinSize(radius) * padding

    fun calculateSize(format: CurrencyFormat) = when (format) {
        is Coin -> calculateCoinSize(format.radius)
        is HoledCoin -> calculateCoinSize(format.radius)
        UndefinedCurrencyFormat -> square(fromCentimeters(1))
    }

    fun calculateCoinSize(radius: Distance): Size2d = square(radius * 2.0f)
}

