package at.orchaldir.gm.visualization.book

import at.orchaldir.gm.core.model.item.book.LeatherBindingType
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
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

    fun calculateSize(height: Distance) = Size2d.square(height + padding * 2.0f)

}