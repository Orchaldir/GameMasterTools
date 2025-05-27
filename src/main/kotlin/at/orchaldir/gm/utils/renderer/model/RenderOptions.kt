package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.utils.math.unit.Distance

data class LineOptions(val color: RenderColor, val width: Distance) {
    constructor(color: RenderColor, width: Float) : this(color, Distance.fromMeters(width))
}


sealed class RenderOptions {

    fun clipping() = when (this) {
        is BorderOnly -> null
        is FillAndBorder -> clipping
        is NoBorder -> null
    }
}

data class FillAndBorder(
    val fill: RenderFill,
    val border: LineOptions,
    val clipping: String? = null,
) : RenderOptions() {

    constructor(color: RenderColor, border: LineOptions, clipping: String? = null) :
            this(RenderSolid(color), border, clipping)

}

data class NoBorder(
    val fill: RenderFill,
) : RenderOptions() {

    constructor(color: RenderColor) : this(RenderSolid(color))

}

data class BorderOnly(val border: LineOptions) : RenderOptions()
