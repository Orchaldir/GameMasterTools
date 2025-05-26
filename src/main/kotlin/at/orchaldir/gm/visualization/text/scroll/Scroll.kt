package at.orchaldir.gm.visualization.text.scroll

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.visualizeSegments

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
    val options = FillAndBorder(color.toRender(), state.config.line)
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
    var startTop = state.aabb.getPoint(HALF, START).addHeight(handleLength)
    var startBottom = state.aabb.getPoint(HALF, END).minusHeight(handleLength)
    val renderer = state.renderer.getLayer()

    handle.segments.forEach { segment ->
        val color = segment.main.getColor(state.state)
        val options = FillAndBorder(color.toRender(), state.config.line)
        val segmentLength = segment.calculateLength(scroll.rollLength)
        val segmentDiameter = segment.calculateDiameter(scroll.rollDiameter)
        val segmentSize = Size2d(segmentDiameter, segmentLength)
        val half = segmentLength / 2

        val centerTop = startTop.minusHeight(half)
        val centerBottom = startBottom.addHeight(half)

        val aabbTop = AABB.fromCenter(centerTop, segmentSize)
        val aabbBottom = AABB.fromCenter(centerBottom, segmentSize)

        visualizeSegments(renderer, options, aabbTop, segment)
        visualizeSegments(renderer, options, aabbBottom, segment)

        startTop = startTop.minusHeight(segmentLength)
        startBottom = startBottom.addHeight(segmentLength)
    }
}



