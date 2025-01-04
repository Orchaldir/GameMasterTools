package at.orchaldir.gm.visualization.book

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.LineOptions

data class BookRenderConfig(
    val padding: Distance,
    val line: LineOptions,
) {

    fun calculateSize(height: Distance) = Size2d.square(height + padding * 2.0f)

}