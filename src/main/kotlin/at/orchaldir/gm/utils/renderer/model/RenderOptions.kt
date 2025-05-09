package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.utils.math.unit.Distance

data class LineOptions(val color: RenderColor, val width: Distance) {
    constructor(color: RenderColor, width: Float) : this(color, Distance.fromMeters(width))
}


sealed class RenderOptions

data class FillAndBorder(
    val fill: RenderFill,
    val border: LineOptions,
) : RenderOptions() {

    constructor(color: RenderColor, border: LineOptions) : this(RenderSolid(color), border)

}

data class NoBorder(
    val fill: RenderFill,
) : RenderOptions() {

    constructor(color: RenderColor) : this(RenderSolid(color))

}

data class BorderOnly(val border: LineOptions) : RenderOptions()
