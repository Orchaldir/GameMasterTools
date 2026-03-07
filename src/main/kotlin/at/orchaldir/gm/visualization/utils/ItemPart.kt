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
) = when (part) {
    is ColorItemPart -> getLineOptions(line, part.getColor(state))
    is ColorSchemeItemPart -> getLineOptions(line, part.getColor(state, colors))
    is FillItemPart -> getLineOptions(line, part.getFill(state))
    is FillLookupItemPart -> getLineOptions(line, part.getFill(state, colors))
    is MadeFromFabric ->  getLineOptions(line, part.getFill(state, colors))
    is MadeFromLeather -> getLineOptions(line, part.getColor(state, colors))
    is MadeFromMetal -> getLineOptions(line, part.getColor(state, colors))
    is MadeFromWood ->  getLineOptions(line, part.getFill(state, colors))
}

fun getLineOptions(line: LineOptions, color: Color) = FillAndBorder(color.toRender(), line)
fun getLineOptions(line: LineOptions, fill: Fill) = FillAndBorder(fill.toRender(), line)