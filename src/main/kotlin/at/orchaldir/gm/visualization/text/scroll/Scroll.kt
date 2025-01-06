package at.orchaldir.gm.visualization.text.scroll

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.ScrollRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeScroll(
    state: TextRenderState,
    scroll: Scroll,
) {
    when (scroll.format) {
        is ScrollWithoutRod -> visualizeRoll(state, scroll)
        is ScrollWithOneRod -> visualizeOneRod(state, scroll, scroll.format)
        is ScrollWithTwoRods -> visualizeTwoRods(state, scroll, scroll.format)
    }
}

private fun visualizeRoll(
    state: TextRenderState,
    scroll: Scroll,
) {
    val options = FillAndBorder(scroll.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

private fun visualizeOneRod(
    state: TextRenderState,
    scroll: Scroll,
    format: ScrollWithOneRod,
) {
    visualizeRod(state, scroll, format.rod)
}

private fun visualizeTwoRods(
    state: TextRenderState,
    scroll: Scroll,
    format: ScrollWithTwoRods,
) {
    val first = state.aabb.splitHorizontal(START, HALF)
    val second = state.aabb.splitHorizontal(HALF, END)

    visualizeRod(state.copy(aabb = first), scroll, format.rod)
    visualizeRod(state.copy(aabb = second), scroll, format.rod)
}

private fun visualizeRod(
    state: TextRenderState,
    scroll: Scroll,
    rod: ScrollRod,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), scroll.calculateRollSize())
    val innerState = state.copy(aabb = inner)

    visualizeRoll(innerState, scroll)

    val handleSize = rod.calculateHandleSize()
    val centerTop = state.aabb.getPoint(HALF, START).addHeight(rod.handleLength / 2)
    val aabbTop = AABB.fromCenter(centerTop, handleSize)

    val options = FillAndBorder(rod.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(aabbTop, options)
}

