package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.Fill
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.RenderFill
import at.orchaldir.gm.utils.renderer.model.RenderSolid
import at.orchaldir.gm.utils.renderer.model.toRender

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
    is ColorItemPart -> convert(state, part, colors)
    is ColorSchemeItemPart -> convert(state, part, colors)
    is FillItemPart -> convert(state, part, colors)
    is FillLookupItemPart -> convert(state, part, colors)
    is MadeFromCord -> convert(state, part, colors)
    is MadeFromFabric ->  convert(state, part, colors)
    is MadeFromLeather -> convert(state, part, colors)
    is MadeFromMetal -> convert(state, part, colors)
    is MadeFromWood ->  convert(state, part, colors)
}

private fun convert(
    state: State,
    hasColor: HasColor,
    colors: Colors,
) = RenderSolid(hasColor.getColor(state, colors).toRender())

private fun convert(
    state: State,
    hasFill: HasFill,
    colors: Colors,
) = hasFill.getFill(state, colors).toRender()
