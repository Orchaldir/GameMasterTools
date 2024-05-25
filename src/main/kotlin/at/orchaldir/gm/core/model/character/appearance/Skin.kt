package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Color
import kotlinx.serialization.Serializable

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
sealed class Skin
data class Scales(val color: Color) : Skin()
data class NormalSkin(val color: SkinColor) : Skin()
data class ExoticSkin(val color: Color) : Skin()