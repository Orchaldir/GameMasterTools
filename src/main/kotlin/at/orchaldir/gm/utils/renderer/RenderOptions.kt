package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Fill
import at.orchaldir.gm.core.model.appearance.Solid
import at.orchaldir.gm.core.model.appearance.VerticalStripes
import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.Serializable

@Serializable
data class LineOptions(val color: RenderColor, val width: Distance) {
    constructor(color: RenderColor, width: Float) : this(color, Distance(width))
}

@Serializable
sealed class RenderOptions

data class FillAndBorder(
    val fill: Fill<RenderColor>,
    val border: LineOptions,
) : RenderOptions() {

    constructor(color: RenderColor, border: LineOptions) : this(Solid(color), border)

}

data class NoBorder(
    val fill: Fill<RenderColor>,
) : RenderOptions() {

    constructor(color: RenderColor) : this(Solid(color))

}
data class BorderOnly(val border: LineOptions) : RenderOptions()

fun Fill<Color>.toRender(): Fill<RenderColor> = when (this) {
    is Solid -> Solid(this.color.toRender())
    is VerticalStripes -> VerticalStripes(this.color0.toRender(), this.color1.toRender(), this.width)
}
