package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.util.FontFamily
import at.orchaldir.gm.core.model.util.VerticalAlignment

data class RenderStringOptions(
    val renderOptions: RenderOptions,
    val size: Float,
    val fontFamily: FontFamily = FontFamily.Arial,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
) {

    constructor(
        color: RenderColor,
        size: Float,
        fontFamily: FontFamily = FontFamily.Arial,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
    ) : this(NoBorder(RenderSolid(color)), size, fontFamily, verticalAlignment)
}
