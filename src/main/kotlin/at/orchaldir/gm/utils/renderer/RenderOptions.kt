package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.Serializable

@Serializable
data class LineOptions(val color: RenderColor, val width: Distance) {
    constructor(color: RenderColor, width: Float) : this(color, Distance(width))
}

@Serializable
sealed class RenderOptions
data class FillAndBorder(
    val fill: RenderColor,
    val border: LineOptions,
) : RenderOptions()

data class NoBorder(val fill: RenderColor) : RenderOptions()
data class BorderOnly(val border: LineOptions) : RenderOptions()
