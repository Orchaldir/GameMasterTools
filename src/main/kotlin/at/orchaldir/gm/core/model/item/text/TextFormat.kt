package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.item.text.book.BookBinding
import at.orchaldir.gm.core.model.item.text.scroll.ScrollFormat
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_TEXT_SIZE = fromCentimeters(1)
val DEFAULT_BOOK_SIZE = fromCentimeters(10)
val DEFAULT_ROLL_LENGTH = fromCentimeters(30)
val DEFAULT_ROLL_DIAMETER = fromCentimeters(5)
val MAX_TEXT_SIZE = fromCentimeters(2000)

val MIN_PAGE_WIDTH_FACTOR = fromPercentage(10)
val DEFAULT_PAGE_WIDTH_FACTOR = HALF
val MAX_PAGE_WIDTH_FACTOR = fromPercentage(1000)

const val MIN_PAGES = 10
const val DEFAULT_PAGES = 100
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
    val pages: Int = DEFAULT_PAGES,
    val page: ColorItemPart = ColorItemPart(),
    val size: Size2d = Size2d.square(DEFAULT_BOOK_SIZE),
) : TextFormat() {

    override fun parts() = binding.parts() + page

}

@Serializable
@SerialName("Scroll")
data class Scroll(
    val format: ScrollFormat,
    val rollLength: Distance = DEFAULT_ROLL_LENGTH,
    val rollDiameter: Distance = DEFAULT_ROLL_DIAMETER,
    val pageWidth: Factor = DEFAULT_PAGE_WIDTH_FACTOR,
    val main: ColorItemPart = ColorItemPart(),
) : TextFormat() {

    fun calculateWidthOfOneRod() = format.calculateWidthOfOneRod(rollDiameter)

    fun calculateHandleLength() = when (format) {
        ScrollWithoutRod -> ZERO_DISTANCE
        is ScrollWithOneRod -> format.handle.calculateHandleLength()
        is ScrollWithTwoRods -> format.handle.calculateHandleLength()
    }

    fun calculatePageWidth() = rollLength * pageWidth

    fun calculatePageSize() = Size2d(calculatePageWidth(), rollLength)
    fun calculateRollSize() = Size2d(rollDiameter, rollLength)

    fun calculateClosedSize(): Size2d {
        val fullLength = format.calculateLength(rollLength)
        val fullWidth = format.calculateWidth(rollDiameter)

        return Size2d(fullWidth, fullLength)
    }

    fun calculateOpenSize(pages: Int): Size2d {
        val pageSize = calculatePageSize()
        val scrollSize = calculateClosedSize()

        return Size2d(scrollSize.width + pageSize.width * pages, scrollSize.height)
    }

    override fun parts() = format.parts() + main

}

@Serializable
@SerialName("Undefined")
data object UndefinedTextFormat : TextFormat()
