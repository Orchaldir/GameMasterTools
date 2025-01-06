package at.orchaldir.gm.visualization.text.scroll

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.ScrollHandle
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.START
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

private fun visualizeRod(
    state: TextRenderState,
    scroll: Scroll,
    handle: ScrollHandle,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), scroll.calculateRollSize())
    val innerState = state.copy(aabb = inner)

    visualizeRoll(innerState, scroll)

    val handleLength = handle.calculateHandleLength()
    var startTop = state.aabb.getPoint(HALF, START).addHeight(handleLength)

    handle.segments.forEach { segment ->
        val options = FillAndBorder(segment.color.toRender(), state.config.line)
        val centerTop = startTop.minusHeight(segment.length / 2)
        val aabbTop = AABB.fromCenter(centerTop, segment.calculateSize())

        state.renderer.getLayer().renderRectangle(aabbTop, options)

        startTop = startTop.minusHeight(segment.length)
    }

    /*
    val centerBottom = state.aabb.getPoint(HALF, END).minusHeight(handle.length / 2)
    val aabbBottom = AABB.fromCenter(centerBottom, handleSize)

    state.renderer.getLayer().renderRectangle(aabbBottom, options)
    */
}

