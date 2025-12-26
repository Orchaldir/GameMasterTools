package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.hair.HairColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SkinType {
    Exotic,
    Fur,
    Material,
    Normal,
    Scales,
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
        is MaterialSkin -> SkinType.Material
        is NormalSkin -> SkinType.Normal
        is Scales -> SkinType.Scales
    }

}

@Serializable
@SerialName("Exotic")
data class ExoticSkin(val color: Color = Color.Red) : Skin()

@Serializable
@SerialName("Fur")
data class Fur(val color: HairColor = NormalHairColor(NormalHairColorEnum.MediumBrown)) : Skin()

@Serializable
@SerialName("Material")
data class MaterialSkin(val material: MaterialId) : Skin()

@Serializable
@SerialName("Normal")
data class NormalSkin(val color: SkinColor = SkinColor.Medium) : Skin()

@Serializable
@SerialName("Scales")
data class Scales(val color: Color = Color.Red) : Skin()