package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FeatureColorType {
    Hair,
    Overwrite,
    Skin,
}

@Serializable
sealed class FeatureColor {

    fun getType() = when (this) {
        ReuseHairColor -> FeatureColorType.Hair
        is OverwriteFeatureColor -> FeatureColorType.Overwrite
        ReuseSkinColor -> FeatureColorType.Skin
    }

}

@Serializable
@SerialName("Hair")
data object ReuseHairColor : FeatureColor()

@Serializable
@SerialName("Overwrite")
data class OverwriteFeatureColor(
    val skin: Skin,
) : FeatureColor() {

    constructor(color: Color) : this(ExoticSkin(color))

}

@Serializable
@SerialName("Skin")
data object ReuseSkinColor : FeatureColor()
