package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.book.BookBinding
import at.orchaldir.gm.core.model.item.text.scroll.ScrollFormat
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
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
sealed class TextFormat : MadeFromParts {

    fun getType() = when (this) {
        is Book -> TextFormatType.Book
        is Scroll -> TextFormatType.Scroll
        UndefinedTextFormat -> TextFormatType.Undefined
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
    val binding: BookBinding,
    val pages: Int = 100,
    val page: ColorItemPart = ColorItemPart(),
    val size: Size2d = Size2d.square(fromMillimeters(100)),
) : TextFormat() {

    override fun parts() = binding.parts() + page

}

@Serializable
@SerialName("Scroll")
data class Scroll(
    val format: ScrollFormat,
    val rollLength: Distance = fromMeters(1),
    val rollDiameter: Distance = fromMillimeters(200),
    val pageWidth: Distance = rollLength / 2.0f,
    val main: ColorItemPart = ColorItemPart(),
) : TextFormat() {

    fun calculatePageSize() = Size2d(pageWidth, rollLength)
    fun calculateRollSize() = Size2d(rollDiameter, rollLength)

    fun calculateSize(): Size2d {
        val fullLength = format.calculateLength(rollLength)
        val fullWidth = format.calculateWidth(rollDiameter)

        return Size2d(fullWidth, fullLength)
    }

    override fun parts() = format.parts() + main

}

@Serializable
@SerialName("Undefined")
data object UndefinedTextFormat : TextFormat()
