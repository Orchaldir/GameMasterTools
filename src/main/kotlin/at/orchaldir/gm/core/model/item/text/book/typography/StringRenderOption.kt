package at.orchaldir.gm.core.model.item.text.book.typography

import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StringRenderOptionType {
    Simple,
    Wrapped,
}

@Serializable
sealed class StringRenderOption {

    fun getType() = when (this) {
        is SimpleStringRenderOption -> StringRenderOptionType.Simple
        is WrappedStringRenderOption -> StringRenderOptionType.Wrapped
    }

    fun getFontOption() = when (this) {
        is SimpleStringRenderOption -> font
        is WrappedStringRenderOption -> font
    }
}

@Serializable
@SerialName("Simple")
data class SimpleStringRenderOption(
    val x: Distance,
    val y: Distance,
    val font: FontOption,
    val orientation: Orientation = Orientation.zero(),
) : StringRenderOption()

@Serializable
@SerialName("Wrapped")
data class WrappedStringRenderOption(
    val x: Distance,
    val y: Distance,
    val font: FontOption,
    val width: Distance,
) : StringRenderOption()
