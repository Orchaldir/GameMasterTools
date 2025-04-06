package at.orchaldir.gm.core.model.character.appearance.hair

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairStyle {
    Long,
    Short,
}

@Serializable
sealed class HairCut {

    fun getType() = when (this) {
        is LongHairCut -> HairStyle.Long
        is ShortHairCut -> HairStyle.Short
    }

}

@Serializable
@SerialName("Short")
data class ShortHairCut(val style: ShortHairStyle) : HairCut()

@Serializable
@SerialName("Long")
data class LongHairCut(
    val style: LongHairStyle,
    val shape: LongHairShape,
    val length: HairLength = HairLength.Classic,
) : HairCut()