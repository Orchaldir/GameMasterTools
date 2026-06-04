package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.utils.RepeatableNumberGenerator
import at.orchaldir.gm.utils.renderer.model.*

fun convertToFillAndBorder(
    colors: Colors,
    numberGenerator: RepeatableNumberGenerator,
    line: LineOptions,
    part: ItemPart,
    state: State,
    clipping: String?,
) = FillAndBorder(
    convertToRenderFill(state, numberGenerator, colors, part),
    line,
    clipping,
)

fun convertToNoBorder(
    state: State,
    numberGenerator: RepeatableNumberGenerator,
    colors: Colors,
    part: ItemPart,
    clipping: String?,
) = NoBorder(
    convertToRenderFill(state, numberGenerator, colors, part),
    clipping
)

fun convertToRenderFill(
    state: State,
    numberGenerator: RepeatableNumberGenerator,
    colors: Colors,
    part: ItemPart,
): RenderFill = when (part) {
    is MadeFromCord -> convert(state, numberGenerator, part, colors)
    is MadeFromFabric -> convertFill(state, numberGenerator, part, colors)
    is MadeFromGem -> convert(state, numberGenerator, part, colors)
    is MadeFromGlass -> {
        val color = part.getColor(state, numberGenerator, colors)
        RenderTransparent(color.toRender(), part.opacity)
    }
    is MadeFromLeather -> convert(state, numberGenerator, part, colors)
    is MadeFromMetal -> convert(state, numberGenerator, part, colors)
    is MadeFromPaper -> convert(state, numberGenerator, part, colors)
    is MadeFromStone -> convert(state, numberGenerator, part, colors)
    is MadeFromWood -> convertFill(state, numberGenerator, part, colors)
}

private fun convert(
    state: State,
    numberGenerator: RepeatableNumberGenerator,
    hasColor: HasColor,
    colors: Colors,
) = RenderSolid(hasColor.getColor(state, numberGenerator, colors).toRender())

private fun convertFill(
    state: State,
    numberGenerator: RepeatableNumberGenerator,
    hasFill: HasFill,
    colors: Colors,
) = hasFill.getFill(state, numberGenerator, colors).toRender()
