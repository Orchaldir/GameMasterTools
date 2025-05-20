package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.util.font.FontWithBorder
import at.orchaldir.gm.core.model.util.font.HollowFont
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.utils.math.unit.Distance

data class RenderStringOptions(
    val renderOptions: RenderOptions,
    val size: Distance,
    val font: Font? = null,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
) {

    constructor(
        color: RenderColor,
        size: Distance,
        font: Font? = null,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
        horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
    ) : this(NoBorder(RenderSolid(color)), size, font, verticalAlignment, horizontalAlignment)
}

fun FontOption.convert(
    state: State,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
) = when (this) {
    is SolidFont -> RenderStringOptions(
        color.toRender(),
        size,
        state.getFontStorage().getOptional(font),
        verticalAlignment,
        horizontalAlignment,
    )

    is FontWithBorder -> RenderStringOptions(
        FillAndBorder(fill.toRender(), LineOptions(border.toRender(), thickness)),
        size,
        state.getFontStorage().getOptional(font),
        verticalAlignment,
        horizontalAlignment,
    )

    is HollowFont -> RenderStringOptions(
        BorderOnly(LineOptions(border.toRender(), thickness)),
        size,
        state.getFontStorage().getOptional(font),
        verticalAlignment,
        horizontalAlignment,
    )
}
