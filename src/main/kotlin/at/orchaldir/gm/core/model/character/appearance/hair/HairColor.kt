package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class NormalHairColorEnum {
    LightestBlond,
    VeryLightBlond,
    LightBlond,
    Blond,
    DarkBlond,
    LightBrown,
    MediumBrown,
    DarkBrown,
    Black,
    Orange,
    Red,
    Auburn,
}

enum class HairColorType {
    None,
    Normal,
    Exotic,
}

@Serializable
sealed class HairColor {

    fun getType() = when (this) {
        is NoHairColor -> HairColorType.None
        is NormalHairColor -> HairColorType.Normal
        is ExoticHairColor -> HairColorType.Exotic
    }

}

@Serializable
@SerialName("None")
data object NoHairColor : HairColor()

@Serializable
@SerialName("Normal")
data class NormalHairColor(
    val color: NormalHairColorEnum,
) : HairColor()

@Serializable
@SerialName("Exotic")
data class ExoticHairColor(
    val color: Color,
) : HairColor()
