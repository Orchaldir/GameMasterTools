package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.renderPolygon
import at.orchaldir.gm.visualization.text.TextRenderState

// L

fun visualizeTopCornerAsL(
    state: TextRenderState,
    options: RenderOptions,
    length: Distance,
    width: Distance,
) {
    val polygon = createTopCornerAsL(state, length, width)
    state.renderer.getLayer().renderPolygon(polygon, options)
}

fun visualizeBottomCornerAsL(
    state: TextRenderState,
    options: RenderOptions,
    length: Distance,
    width: Distance,
) {
    val polygon = createTopCornerAsL(state, length, width)
    val mirrored = state.aabb.mirrorHorizontally(polygon)

    state.renderer.getLayer().renderPolygon(mirrored, options)
}

private fun createTopCornerAsL(
    state: TextRenderState,
    length: Distance,
    width: Distance,
): Polygon2d {
    val topRight = state.aabb.getPoint(END, START)
    val bottomRight = topRight.addHeight(length)
    val topLeft = topRight.minusWidth(length)
    val innerLeft = topLeft.addHeight(width)
    val innerRight = bottomRight.minusWidth(width)
    val inner = topRight.minusWidth(width).addHeight(width)

    return Polygon2d(listOf(topRight, bottomRight, innerRight, inner, innerLeft, topLeft))
}

// round

fun visualizeRoundTopCorner(
    state: TextRenderState,
    options: RenderOptions,
    length: Distance,
) {
    val polygon = createRoundTopCorner(state, length)

    state.renderer.getLayer().renderRoundedPolygon(polygon, options)
}

fun visualizeRoundBottomCorner(
    state: TextRenderState,
    options: RenderOptions,
    length: Distance,
) {
    val polygon = createRoundTopCorner(state, length)
    val mirrored = state.aabb.mirrorHorizontally(polygon)

    state.renderer.getLayer().renderRoundedPolygon(mirrored, options)
}

private fun createRoundTopCorner(
    state: TextRenderState,
    length: Distance,
): Polygon2d {
    val topRight = state.aabb.getPoint(END, START)
    val bottomRight = topRight.addHeight(length)
    val topLeft = topRight.minusWidth(length)
    val bottomLeft = topLeft.addHeight(length)

    return Polygon2d(
        listOf(
            topRight, topRight,
            bottomRight, bottomRight,
            bottomLeft,
            topLeft, topLeft,
        )
    )
}

// triangle

fun visualizeTopCornerAsTriangle(
    state: TextRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, START)
    val corner1 = corner0.addHeight(distance)
    val corner2 = corner0.minusWidth(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}

fun visualizeBottomCornerAsTriangle(
    state: TextRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, END)
    val corner1 = corner0.minusWidth(distance)
    val corner2 = corner0.minusHeight(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}
