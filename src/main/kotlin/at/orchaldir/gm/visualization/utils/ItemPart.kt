package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.Fill
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.toRender

fun convertToRenderOptions(
    colors: Colors,
    line: LineOptions, 
    part: ItemPart,
    state: State,
    clipping: String?,
) = when (part) {
    is ColorItemPart -> getLineOptions(line, part.getColor(state), clipping)
    is ColorSchemeItemPart -> getLineOptions(line, part.getColor(state, colors), clipping)
    is FillItemPart -> getLineOptions(line, part.getFill(state), clipping)
    is FillLookupItemPart -> getLineOptions(line, part.getFill(state, colors), clipping)
    is MadeFromCord -> getLineOptions(line, part.getColor(state, colors), clipping)
    is MadeFromFabric ->  getLineOptions(line, part.getFill(state, colors), clipping)
    is MadeFromLeather -> getLineOptions(line, part.getColor(state, colors), clipping)
    is MadeFromMetal -> getLineOptions(line, part.getColor(state, colors), clipping)
    is MadeFromWood ->  getLineOptions(line, part.getFill(state, colors), clipping)
}

fun getLineOptions(line: LineOptions, color: Color, clipping: String?) =
    FillAndBorder(color.toRender(), line, clipping)
fun getLineOptions(line: LineOptions, fill: Fill, clipping: String?) =
    FillAndBorder(fill.toRender(), line, clipping)