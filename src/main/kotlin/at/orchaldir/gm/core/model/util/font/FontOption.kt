package at.orchaldir.gm.core.model.util.font

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FontOptionType {
    Solid,
    Border,
    Hollow,
}

@Serializable
sealed class FontOption {

    fun getType() = when (this) {
        is SolidFont -> FontOptionType.Solid
        is FontWithBorder -> FontOptionType.Border
        is HollowFont -> FontOptionType.Hollow
    }

    fun font() = when (this) {
        is SolidFont -> font
        is FontWithBorder -> font
        is HollowFont -> font
    }

    fun getFontSize() = when (this) {
        is SolidFont -> size
        is FontWithBorder -> size
        is HollowFont -> size
    }
}

@Serializable
@SerialName("Solid")
data class SolidFont(
    val size: Distance,
    val color: Color = Color.Black,
    val font: FontId? = null,
) : FontOption()

@Serializable
@SerialName("Border")
data class FontWithBorder(
    val size: Distance,
    val thickness: Distance,
    val fill: Color = Color.White,
    val border: Color = Color.Black,
    val font: FontId? = null,
) : FontOption()

@Serializable
@SerialName("Hollow")
data class HollowFont(
    val size: Distance,
    val thickness: Distance,
    val border: Color = Color.Black,
    val font: FontId? = null,
) : FontOption()
