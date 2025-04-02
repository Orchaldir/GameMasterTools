package at.orchaldir.gm.core.model.character.appearance.tail

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TailColorType {
    Hair,
    Overwrite,
    Skin,
}

@Serializable
sealed class TailColor {

    fun getType() = when (this) {
        ReuseHairColor -> TailColorType.Hair
        is OverwriteTailColor -> TailColorType.Overwrite
        ReuseSkinColor -> TailColorType.Skin
    }

}

@Serializable
@SerialName("Hair")
data object ReuseHairColor : TailColor()

@Serializable
@SerialName("Overwrite")
data class OverwriteTailColor(
    val color: Color,
) : TailColor()

@Serializable
@SerialName("Skin")
data object ReuseSkinColor : TailColor()
