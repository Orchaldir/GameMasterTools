package at.orchaldir.gm.visualization.book

import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.core.model.item.text.Codex
import at.orchaldir.gm.core.model.item.text.LeatherBindingType
import at.orchaldir.gm.core.model.item.text.UndefinedTextFormat
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig

data class LeatherBindingConfig(
    val spine: Factor,
    val corner: Factor,
)

data class BookRenderConfig(
    val padding: Distance,
    val line: LineOptions,
    val leatherBindingMap: Map<LeatherBindingType, LeatherBindingConfig>,
    val sewingRadius: SizeConfig<Factor>,
    val sewingLength: SizeConfig<Factor>,
) {

    fun calculateSize(book: TextFormat) = when (book) {
        is Codex -> book.size.toSize2d() + (padding * 2)
        UndefinedTextFormat -> square(padding * 4.0f)
    }

}