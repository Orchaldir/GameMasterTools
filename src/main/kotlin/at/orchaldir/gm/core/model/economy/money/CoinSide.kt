package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.util.font.FontId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CoinSideType {
    Blank,
    Denomination,
    Name,
    Number,
    Value,
}

@Serializable
sealed class CoinSide {

    fun getType() = when (this) {
        is BlankCoinSide -> CoinSideType.Blank
        is ShowDenomination -> CoinSideType.Denomination
        is ShowName -> CoinSideType.Name
        is ShowNumber -> CoinSideType.Number
        is ShowValue -> CoinSideType.Value
    }

    fun font() = when (this) {
        is BlankCoinSide -> null
        is ShowDenomination -> font
        is ShowName -> font
        is ShowNumber -> font
        is ShowValue -> font
    }
}

@Serializable
@SerialName("Blank")
data object BlankCoinSide : CoinSide()

@Serializable
@SerialName("Denomination")
data class ShowDenomination(
    val font: FontId? = null,
) : CoinSide()

@Serializable
@SerialName("Name")
data class ShowName(
    val font: FontId? = null,
) : CoinSide()

@Serializable
@SerialName("Number")
data class ShowNumber(
    val font: FontId? = null,
) : CoinSide()

@Serializable
@SerialName("Value")
data class ShowValue(
    val font: FontId? = null,
) : CoinSide()
