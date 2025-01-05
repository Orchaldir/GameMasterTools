package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.utils.math.Size2i
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_PAGES = 10

enum class TextFormatType {
    Codex,
    Undefined,
}

@Serializable
sealed class TextFormat {

    fun getType() = when (this) {
        is Codex -> TextFormatType.Codex
        UndefinedTextFormat -> TextFormatType.Undefined
    }
}

@Serializable
@SerialName("Codex")
data class Codex(
    val pages: Int,
    val binding: BookBinding,
    val size: Size2i = Size2i.square(100),
) : TextFormat()

@Serializable
@SerialName("Undefined")
data object UndefinedTextFormat : TextFormat()
