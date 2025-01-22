package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.util.Side
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairStyleType {
    BuzzCut,
    FlatTop,
    MiddlePart,
    Shaved,
    SidePart,
    Spiked,
}

@Serializable
sealed class HairStyle

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