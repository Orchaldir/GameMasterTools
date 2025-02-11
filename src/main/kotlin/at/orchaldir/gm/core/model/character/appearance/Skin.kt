package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SkinType {
    Fur,
    Scales,
    Normal,
    Exotic,
}

@Serializable
enum class SkinColor {
    Fair,
    Light,
    Medium,
    Tan,
    Dark,
    VeryDark,
}

@Serializable
sealed class Skin {

    fun getType() = when (this) {
        is ExoticSkin -> SkinType.Exotic
        is Fur -> SkinType.Fur
        is NormalSkin -> SkinType.Normal
        is Scales -> SkinType.Scales
    }

}

@Serializable
@SerialName("Fur")
data class Fur(val color: Color = Color.SaddleBrown) : Skin()

@Serializable
@SerialName("Scales")
data class Scales(val color: Color = Color.Red) : Skin()

@Serializable
@SerialName("Normal")
data class NormalSkin(val color: SkinColor = SkinColor.Medium) : Skin()

@Serializable
@SerialName("Exotic")
data class ExoticSkin(val color: Color = Color.Red) : Skin()