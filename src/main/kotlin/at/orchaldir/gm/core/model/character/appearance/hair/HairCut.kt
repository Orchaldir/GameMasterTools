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
        is Ponytail -> HairStyle.Ponytail
        is ShortHairCut -> HairStyle.Short
    }

}

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
    val position: PonytailPosition = PonytailPosition.Low,
    val length: HairLength = HairLength.Classic,
) : HairCut()

@Serializable
@SerialName("Short")
data class ShortHairCut(val style: ShortHairStyle) : HairCut()