package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.item.text.book.BookBinding
import at.orchaldir.gm.core.model.item.text.scroll.ScrollFormat
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2i
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_PAGES = 10

enum class TextFormatType {
    Book,
    Scroll,
    Undefined,
}

@Serializable
sealed class TextFormat {

    fun getType() = when (this) {
        is Book -> TextFormatType.Book
        is Scroll -> TextFormatType.Scroll
        UndefinedTextFormat -> TextFormatType.Undefined
    }
}

@Serializable
@SerialName("Book")
data class Book(
    val pages: Int,
    val binding: BookBinding,
    val size: Size2i = Size2i.square(100),
) : TextFormat()

@Serializable
@SerialName("Scroll")
data class Scroll(
    val vertical: Boolean,
    val rollLength: Distance,
    val rollDiameter: Distance,
    val format: ScrollFormat,
) : TextFormat() {

    fun calculateSize(): Size2d {
        val fullLength = format.calculateLength(rollLength)
        val fullWidth = format.calculateWidth(rollLength)

        return if (vertical) {
            Size2d(fullLength, fullWidth)
        } else {
            Size2d(fullWidth, fullLength)
        }
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedTextFormat : TextFormat()
