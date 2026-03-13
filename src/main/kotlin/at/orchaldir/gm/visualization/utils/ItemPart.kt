package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.utils.renderer.model.*

fun convertToFillAndBorder(
    colors: Colors,
    line: LineOptions,
    part: ItemPart,
    state: State,
    clipping: String?,
) = FillAndBorder(
    convertToRenderFill(state, colors, part),
    line,
    clipping,
)

fun convertToNoBorder(
    state: State,
    colors: Colors,
    part: ItemPart,
    clipping: String?,
) = NoBorder(
    convertToRenderFill(state, colors, part),
    clipping
)

fun convertToRenderFill(
    state: State,
    colors: Colors,
    part: ItemPart,
): RenderFill = when (part) {
    is MadeFromCord -> convert(state, part, colors)
    is MadeFromFabric -> convertFill(state, part, colors)
    is MadeFromGem -> convert(state, part, colors)
    is MadeFromGlass -> RenderTransparent(part.getColor(state, colors).toRender(), part.opacity)
    is MadeFromLeather -> convert(state, part, colors)
    is MadeFromMetal -> convert(state, part, colors)
    is MadeFromPaper -> convert(state, part, colors)
    is MadeFromWood -> convertFill(state, part, colors)
}

private fun convert(
    state: State,
    hasColor: HasColor,
    colors: Colors,
) = RenderSolid(hasColor.getColor(state, colors).toRender())

private fun convertFill(
    state: State,
    hasFill: HasFill,
    colors: Colors,
) = hasFill.getFill(state, colors).toRender()
