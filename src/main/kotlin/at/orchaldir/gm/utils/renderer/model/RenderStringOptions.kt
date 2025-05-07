package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.FontWithBorder
import at.orchaldir.gm.core.model.font.HollowFont
import at.orchaldir.gm.core.model.font.SolidFont
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

fun FontOption.convert(
    state: State,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
) = when (this) {
    is SolidFont -> RenderStringOptions(
        color.toRender(),
        size.toMeters(),
        state.getFontStorage().getOptional(font),
        verticalAlignment
    )

    is FontWithBorder -> RenderStringOptions(
        FillAndBorder(fill.toRender(), LineOptions(border.toRender(), thickness)),
        size.toMeters(),
        state.getFontStorage().getOptional(font),
        verticalAlignment,
    )

    is HollowFont -> RenderStringOptions(
        BorderOnly(LineOptions(border.toRender(), thickness)),
        size.toMeters(),
        state.getFontStorage().getOptional(font),
        verticalAlignment,
    )
}
