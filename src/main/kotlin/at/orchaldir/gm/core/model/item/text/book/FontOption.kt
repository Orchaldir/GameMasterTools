package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
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
}

@Serializable
@SerialName("Solid")
data class SolidFont(
    val color: Color = Color.White,
    val size: Distance,
) : FontOption()

@Serializable
@SerialName("Border")
data class FontWithBorder(
    val fill: Color = Color.White,
    val border: Color = Color.Black,
    val size: Distance,
    val thickness: Distance,
) : FontOption()
