package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
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
    val fontOption: FontOption,
    val orientation: Orientation = Orientation.zero(),
    val width: Distance? = null,
) : TextRenderOption()
