package at.orchaldir.gm.core.model.item.text.scroll

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ScrollFormatType {
    NoRod,
    OneRod,
    TwoRods,
}

@Serializable
sealed class ScrollFormat {

    fun getType() = when (this) {
        is NoRod -> ScrollFormatType.NoRod
        is OneRod -> ScrollFormatType.OneRod
        is TwoRods -> ScrollFormatType.TwoRods
    }
}

@Serializable
@SerialName("NoRod")
data object NoRod : ScrollFormat()

@Serializable
@SerialName("OneRod")
data class OneRod(
    val rod: ScrollRod,
) : ScrollFormat()

@Serializable
@SerialName("TwoRods")
data class TwoRods(
    val rod: ScrollRod,
) : ScrollFormat()
