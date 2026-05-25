package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.utils.convertToFillAndBorder
import at.orchaldir.gm.visualization.utils.convertToNoBorder

data class GrammarRenderState(
    val state: State,
    val renderer: MultiLayerRenderer,
    val line: LineOptions,
    val colors: Colors = UndefinedColors
) : RenderState {

    override fun state() = state
    override fun renderer() = renderer
    override fun lineOptions() = line

    override fun getFillAndBorder(part: ItemPart, clipping: String?) = convertToFillAndBorder(
        colors,
        lineOptions(),
        part,
        state,
        clipping,
    )

    override fun getNoBorder(part: ItemPart, clipping: String?) = convertToNoBorder(
        state,
        colors,
        part,
        clipping,
    )
}
