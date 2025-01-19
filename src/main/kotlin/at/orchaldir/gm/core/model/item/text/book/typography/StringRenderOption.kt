package at.orchaldir.gm.core.model.item.text.book.typography

import at.orchaldir.gm.core.model.util.FontOption
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
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
}

@Serializable
@SerialName("Simple")
data class SimpleStringRenderOption(
    val x: Distance,
    val y: Distance,
    val fontOption: FontOption,
    val orientation: Orientation = Orientation.zero(),
) : StringRenderOption()

@Serializable
@SerialName("Wrapped")
data class WrappedStringRenderOption(
    val x: Distance,
    val y: Distance,
    val fontOption: FontOption,
    val width: Distance,
) : StringRenderOption()
