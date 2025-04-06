package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment

data class RenderStringOptions(
    val renderOptions: RenderOptions,
    val size: Float,
    val font: Font? = null,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
) {

    constructor(
        color: RenderColor,
        size: Float,
        font: Font? = null,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
        horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
    ) : this(NoBorder(RenderSolid(color)), size, font, verticalAlignment, horizontalAlignment)
}
