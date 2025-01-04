package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.utils.math.Size2i
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookFormatType {
    Codex,
    Undefined,
}

@Serializable
sealed class BookFormat {

    fun getType() = when (this) {
        is Codex -> BookFormatType.Codex
        UndefinedBookFormat -> BookFormatType.Undefined
    }
}

@Serializable
@SerialName("Codex")
data class Codex(
    val pages: Int,
    val binding: BookBinding,
    val size: Size2i,
) : BookFormat()

@Serializable
@SerialName("Undefined")
data object UndefinedBookFormat : BookFormat()
