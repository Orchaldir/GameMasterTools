package at.orchaldir.gm.core.model.font

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FontOptionType {
    Solid,
    Border,
}

@Serializable
sealed class FontOption {

    fun getType() = when (this) {
        is SolidFont -> FontOptionType.Solid
        is FontWithBorder -> FontOptionType.Border
    }

    fun font() = when (this) {
        is SolidFont -> font
        is FontWithBorder -> font
    }

    fun getFontSize() = when (this) {
        is SolidFont -> size
        is FontWithBorder -> size
    }
}

@Serializable
@SerialName("Solid")
data class SolidFont(
    val size: Distance,
    val color: Color = Color.White,
    val font: FontId = FontId(0),
) : FontOption()

@Serializable
@SerialName("Border")
data class FontWithBorder(
    val size: Distance,
    val thickness: Distance,
    val fill: Color = Color.White,
    val border: Color = Color.Black,
    val font: FontId = FontId(0),
) : FontOption()
