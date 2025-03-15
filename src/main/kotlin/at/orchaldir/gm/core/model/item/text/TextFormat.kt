package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.text.book.BookBinding
import at.orchaldir.gm.core.model.item.text.scroll.ScrollFormat
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_PAGES = 10
const val MIN_CONTENT_PAGES = 1

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

    fun isMadeOf(material: MaterialId) = when (this) {
        is Book -> binding.isMadeOf(material)
        is Scroll -> this.material == material || format.isMadeOf(material)
        UndefinedTextFormat -> false
    }

    fun contains(font: FontId) = when (this) {
        is Book -> binding.contains(font)
        is Scroll -> false
        UndefinedTextFormat -> false
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
    val format: ScrollFormat,
    val rollLength: Distance = Distance(1000),
    val rollDiameter: Distance = Distance(200),
    val color: Color = Color.Yellow,
    val material: MaterialId = MaterialId(0),
) : TextFormat() {

    fun calculateRollSize() = Size2d(rollDiameter, rollLength)

    fun calculateSize(): Size2d {
        val fullLength = format.calculateLength(rollLength)
        val fullWidth = format.calculateWidth(rollDiameter)

        return Size2d(fullWidth, fullLength)
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedTextFormat : TextFormat()
