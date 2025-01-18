package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StringRenderOptionType {
    Simple,
}

@Serializable
sealed class StringRenderOption {

    fun getType() = when (this) {
        is SimpleStringRenderOption -> StringRenderOptionType.Simple
    }
}

@Serializable
@SerialName("Simple")
data class SimpleStringRenderOption(
    val x: Distance,
    val y: Distance,
    val fontOption: FontOption,
    val orientation: Orientation = Orientation.zero(),
    val width: Distance? = null,
) : StringRenderOption()
