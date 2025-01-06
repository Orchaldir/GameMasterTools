package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.book.LeatherBindingType
import at.orchaldir.gm.core.model.item.text.UndefinedTextFormat
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig

data class LeatherBindingConfig(
    val spine: Factor,
    val corner: Factor,
)

data class TextRenderConfig(
    val padding: Distance,
    val line: LineOptions,
    val leatherBindingMap: Map<LeatherBindingType, LeatherBindingConfig>,
    val sewingRadius: SizeConfig<Factor>,
    val sewingLength: SizeConfig<Factor>,
) {

    fun calculatePaddedSize(format: TextFormat) = calculateSize(format) + padding * 2

    fun calculateSize(format: TextFormat) = when (format) {
        is Book -> format.size.toSize2d()
        is Scroll -> format.calculateSize()

        UndefinedTextFormat -> square(padding * 2)
    }

}