package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.font.FontId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CoinSideType {
    Blank,
    Denomination,
    Name,
}

@Serializable
sealed class CoinSide {

    fun getType() = when (this) {
        is BlankCoinSide -> CoinSideType.Blank
        is ShowDenomination -> CoinSideType.Denomination
        is ShowName -> CoinSideType.Name
    }
}

@Serializable
@SerialName("Blank")
data object BlankCoinSide : CoinSide()

@Serializable
@SerialName("Denomination")
data class ShowDenomination(
    val font: FontId,
) : CoinSide()

@Serializable
@SerialName("Name")
data class ShowName(
    val font: FontId,
) : CoinSide()
