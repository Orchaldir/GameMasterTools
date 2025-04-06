package at.orchaldir.gm.core.model.character.appearance.hair

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairStyle {
    Short,
}

@Serializable
sealed class HairCut {

    fun getType() = when (this) {
        is ShortHairCut -> HairStyle.Short
    }

}

@Serializable
@SerialName("Short")
data class ShortHairCut(val style: ShortHairStyle) : HairCut()