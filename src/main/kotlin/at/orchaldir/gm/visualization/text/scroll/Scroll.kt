package at.orchaldir.gm.visualization.text.scroll

import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.scroll.*
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
    var startBottom = state.aabb.getPoint(HALF, END).minusHeight(handleLength)
    val renderer = state.renderer.getLayer()

    handle.segments.forEach { segment ->
        val color = segment.main.getColor(state.state)
        val options = FillAndBorder(color.toRender(), state.config.line)
        val half = segment.length / 2

        val centerTop = startTop.minusHeight(half)
        val centerBottom = startBottom.addHeight(half)

        val aabbTop = AABB.fromCenter(centerTop, segment.calculateSize())
        val aabbBottom = AABB.fromCenter(centerBottom, segment.calculateSize())

        when (segment.shape) {
            HandleSegmentShape.Cone -> {
                val builderTop = Polygon2dBuilder()
                val builderBottom = Polygon2dBuilder()

                builderTop.addMirroredPoints(aabbTop, FULL, END)
                builderBottom.addMirroredPoints(aabbBottom, FULL, START)

                builderTop.addLeftPoint(aabbTop, CENTER, START)
                builderBottom.addLeftPoint(aabbBottom, CENTER, END)

                renderer.renderPolygon(builderTop.build(), options)
                renderer.renderPolygon(builderBottom.build(), options)
            }

            HandleSegmentShape.Cylinder -> {
                renderer.renderRectangle(aabbTop, options)
                renderer.renderRectangle(aabbBottom, options)
            }

            HandleSegmentShape.RoundedCylinder -> {
                renderer.renderRoundedPolygon(Polygon2d(aabbTop.getCorners()), options)
                renderer.renderRoundedPolygon(Polygon2d(aabbBottom.getCorners()), options)
            }

            HandleSegmentShape.Sphere -> {
                renderer.renderEllipse(aabbTop, options)
                renderer.renderEllipse(aabbBottom, options)
            }
        }

        startTop = startTop.minusHeight(segment.length)
        startBottom = startBottom.addHeight(segment.length)
    }
}

