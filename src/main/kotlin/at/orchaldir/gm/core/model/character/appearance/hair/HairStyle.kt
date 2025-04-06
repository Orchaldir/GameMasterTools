package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.util.Side
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairStyleType {
    BowlCut,
    BuzzCut,
    FlatTop,
    MiddlePart,
    Shaved,
    SidePart,
    Spiked,
}

@Serializable
sealed class HairStyle {

    fun getType() = when (this) {
        BowlCut -> HairStyleType.BowlCut
        BuzzCut -> HairStyleType.BuzzCut
        FlatTop -> HairStyleType.FlatTop
        MiddlePart -> HairStyleType.MiddlePart
        ShavedHair -> HairStyleType.Shaved
        is SidePart -> HairStyleType.SidePart
        Spiked -> HairStyleType.Spiked
    }

}

@Serializable
@SerialName("BowlCut")
data object BowlCut : HairStyle()

@Serializable
@SerialName("BuzzCut")
data object BuzzCut : HairStyle()

@Serializable
@SerialName("FlatTop")
data object FlatTop : HairStyle()

@Serializable
@SerialName("MiddlePart")
data object MiddlePart : HairStyle()

@Serializable
@SerialName("Shaved")
data object ShavedHair : HairStyle()

@Serializable
@SerialName("SidePart")
data class SidePart(val side: Side) : HairStyle()

@Serializable
@SerialName("Spiked")
data object Spiked : HairStyle()