package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.utils.math.unit.Distance

data class LineOptions(val color: RenderColor, val width: Distance) {
    constructor(color: RenderColor, width: Float) : this(color, Distance.fromMeters(width))
}


sealed class RenderOptions {

    fun clipping() = when (this) {
        is BorderOnly -> clipping
        is FillAndBorder -> clipping
        is NoBorder -> clipping
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
    val clipping: String? = null,
) : RenderOptions() {

    constructor(color: RenderColor, clipping: String? = null) : this(RenderSolid(color), clipping)

}

data class BorderOnly(
    val border: LineOptions,
    val clipping: String? = null,
) : RenderOptions()
