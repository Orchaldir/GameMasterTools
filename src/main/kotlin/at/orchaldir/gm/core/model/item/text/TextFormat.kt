package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.book.BookBinding
import at.orchaldir.gm.core.model.item.text.scroll.ScrollFormat
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.checkFactor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_PAGE_WIDTH_FACTOR = fromPercentage(10)
val DEFAULT_PAGE_WIDTH_FACTOR = HALF
val MAX_PAGE_WIDTH_FACTOR = fromPercentage(1000)
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
    val rollLength: Distance = fromCentimeters(30),
    val rollDiameter: Distance = fromCentimeters(5),
    val pageWidth: Factor = DEFAULT_PAGE_WIDTH_FACTOR,
    val main: ColorItemPart = ColorItemPart(),
) : TextFormat() {

    init {
        checkFactor(pageWidth, "page width", MIN_PAGE_WIDTH_FACTOR, MAX_PAGE_WIDTH_FACTOR)
    }

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
