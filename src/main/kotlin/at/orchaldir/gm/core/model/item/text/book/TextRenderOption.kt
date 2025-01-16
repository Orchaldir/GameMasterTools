package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TextRenderOptionType {
    Simple,
}

@Serializable
sealed class TextRenderOption {

    fun getType() = when (this) {
        is SimpleTextRenderOption -> TextRenderOptionType.Simple
    }
}

@Serializable
@SerialName("Simple")
data class SimpleTextRenderOption(
    val x: Distance,
    val y: Distance,
    val size: Distance,
    val color: Color = Color.White,
) : TextRenderOption()
