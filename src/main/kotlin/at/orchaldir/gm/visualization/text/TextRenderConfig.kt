package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.core.model.item.text.UndefinedTextFormat
import at.orchaldir.gm.core.model.item.text.book.LeatherBindingStyle
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.core.model.util.SizeConfig

data class LeatherBindingConfig(
    val spine: Factor,
    val corner: Factor,
)

data class TextRenderConfig(
    val padding: Distance,
    val line: LineOptions,
    val leatherBindingMap: Map<LeatherBindingStyle, LeatherBindingConfig>,
    val bossesRadius: SizeConfig<Factor>,
    val sewingRadius: SizeConfig<Factor>,
    val sewingLength: SizeConfig<Factor>,
    val exampleStrings: List<String>,
    val lastPageFillFactor: Factor,
) {

    fun calculatePaddedClosedSize(format: TextFormat) = addPadding(calculateClosedSize(format))

    fun addPadding(size: Size2d) = size + padding * 2

    fun calculateClosedSize(format: TextFormat) = when (format) {
        is Book -> format.size
        is Scroll -> format.calculateClosedSize()
        UndefinedTextFormat -> square(padding * 2)
    }

    fun calculateOpenSize(format: TextFormat) = when (format) {
        is Book -> format.size
        is Scroll -> format.calculateOpenSize(1)
        UndefinedTextFormat -> square(padding * 2)
    }

}