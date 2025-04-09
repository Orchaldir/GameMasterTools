package at.orchaldir.gm.core.model.character.appearance.hair

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairStyle {
    Long,
    Ponytail,
    Short,
}

@Serializable
sealed class HairCut {

    fun getType() = when (this) {
        is LongHairCut -> HairStyle.Long
        is ShortHairCut -> HairStyle.Short
        is Ponytail -> HairStyle.Ponytail
    }

}

@Serializable
@SerialName("Short")
data class ShortHairCut(val style: ShortHairStyle) : HairCut()

@Serializable
@SerialName("Long")
data class LongHairCut(
    val style: LongHairStyle,
    val length: HairLength = HairLength.Classic,
) : HairCut()

@Serializable
@SerialName("Ponytail")
data class Ponytail(
    val style: PonytailStyle = PonytailStyle.Straight,
    val length: HairLength = HairLength.Classic,
    val position: PonytailPosition = PonytailPosition.Low,
) : HairCut()