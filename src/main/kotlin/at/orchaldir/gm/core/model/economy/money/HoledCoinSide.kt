package at.orchaldir.gm.core.model.economy.money

import kotlinx.serialization.Serializable

@Serializable
data class HoledCoinSide(
    val top: CoinSide = BlankCoinSide,
    val center: CoinSide = BlankCoinSide,
    val bottom: CoinSide = BlankCoinSide,
)