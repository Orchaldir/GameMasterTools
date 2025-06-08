package at.orchaldir.gm.visualization.text.scroll

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.utils.visualizeSegments

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

fun visualizeOpenScroll(
    state: TextRenderState,
    scroll: Scroll,
) {
    when (scroll.format) {
        is ScrollWithoutRod -> visualizeRoll(left(state, scroll), scroll)
        is ScrollWithOneRod -> visualizeOneRod(left(state, scroll), scroll, scroll.format)
        is ScrollWithTwoRods -> {
            visualizeRod(left(state, scroll), scroll, scroll.format.handle)
            visualizeRod(right(state, scroll), scroll, scroll.format.handle)
        }
    }
}

private fun left(
    state: TextRenderState,
    scroll: Scroll,
) = state.copy(aabb = state.aabb.replaceWidth(scroll.calculateWidthOfOneRod()))

private fun right(
    state: TextRenderState,
    scroll: Scroll,
): TextRenderState {
    val size = state.aabb.size.replaceWidth(scroll.calculateWidthOfOneRod())
    val start = state.aabb.getPoint(FULL, START).minusWidth(size.width)

    return state.copy(aabb = AABB(start, size))
}

private fun visualizeRoll(
    state: TextRenderState,
    scroll: Scroll,
) {
    val color = scroll.main.getColor(state.state)
    val options = state.config.getLineOptions(color)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

private fun visualizeOneRod(
    state: TextRenderState,
    scroll: Scroll,
    format: ScrollWithOneRod,
) {
    visualizeRod(state, scroll, format.handle)
}

private fun visualizeTwoRods(
    state: TextRenderState,
    scroll: Scroll,
    format: ScrollWithTwoRods,
) {
    val first = state.aabb.splitHorizontal(START, HALF)
    val second = state.aabb.splitHorizontal(HALF, END)

    visualizeRod(state.copy(aabb = first), scroll, format.handle)
    visualizeRod(state.copy(aabb = second), scroll, format.handle)
}

private fun visualizeTwoOpenRods(
    state: TextRenderState,
    scroll: Scroll,
    format: ScrollWithTwoRods,
) {
    val first = state.aabb.splitHorizontal(START, HALF)
    val second = state.aabb.splitHorizontal(HALF, END)

    visualizeRod(state.copy(aabb = first), scroll, format.handle)
    visualizeRod(state.copy(aabb = second), scroll, format.handle)
}

private fun visualizeRod(
    state: TextRenderState,
    scroll: Scroll,
    handle: Segments,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), scroll.calculateRollSize())
    val innerState = state.copy(aabb = inner)

    visualizeRoll(innerState, scroll)

    val handleLength = handle.calculateLength(scroll.rollLength)
    val startTop = state.aabb.getPoint(HALF, START).addHeight(handleLength)
    val startBottom = state.aabb.getPoint(HALF, END).minusHeight(handleLength)

    visualizeSegments(state, handle, startTop, true, scroll.rollLength, scroll.rollDiameter)
    visualizeSegments(state, handle, startBottom, false, scroll.rollLength, scroll.rollDiameter)
}



