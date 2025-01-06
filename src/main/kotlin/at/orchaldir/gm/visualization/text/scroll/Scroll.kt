package at.orchaldir.gm.visualization.text.scroll

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.NoRod
import at.orchaldir.gm.core.model.item.text.scroll.OneRod
import at.orchaldir.gm.core.model.item.text.scroll.TwoRods
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeScroll(
    state: TextRenderState,
    scroll: Scroll,
) {
    when (scroll.format) {
        is NoRod -> visualizeNoRod(state, scroll)
        is OneRod -> doNothing()
        is TwoRods -> doNothing()
    }
}

private fun visualizeNoRod(
    state: TextRenderState,
    scroll: Scroll,
) {
    val options = FillAndBorder(scroll.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

