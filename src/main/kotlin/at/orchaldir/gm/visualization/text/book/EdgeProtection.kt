package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.visualization.renderPolygon
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeEdgeProtection(
    state: TextRenderState,
    protection: EdgeProtection,
) {
    when (protection) {
        NoEdgeProtection -> doNothing()
        is ProtectedCorners -> visualizeProtectedCorners(state, protection)
        is ProtectedEdge -> visualizeProtectedEdge(state, protection)
    }
}

private fun visualizeProtectedCorners(
    state: TextRenderState,
    data: ProtectedCorners,
) {
    val color = data.main.getColor(state.state)
    val options = state.config.getLineOptions(color)
    val length = state.aabb.convertMinSide(data.size)

    when (data.shape) {
        CornerShape.L -> {
            val width = length * 0.2f
            visualizeTopCornerAsL(state, options, length, width)
            visualizeBottomCornerAsL(state, options, length, width)
        }

        CornerShape.Round -> {
            visualizeRoundTopCorner(state, options, length)
            visualizeRoundBottomCorner(state, options, length)
        }

        CornerShape.Triangle -> {
            visualizeTopCornerAsTriangle(state, options, length)
            visualizeBottomCornerAsTriangle(state, options, length)
        }
    }
}

private fun visualizeProtectedEdge(
    state: TextRenderState,
    data: ProtectedEdge,
) {
    val color = data.main.getColor(state.state)
    val options = state.config.getLineOptions(color)
    val width = state.aabb.convertMinSide(data.width)

    val topLeft = state.aabb.getPoint(START, START)
    val topRight = state.aabb.getPoint(END, START)
    val bottomLeft = state.aabb.getPoint(START, END)
    val bottomRight = state.aabb.getPoint(END, END)

    val innerTopLeft = topLeft.addHeight(width)
    val innerTopRight = topRight.addHeight(width).minusWidth(width)
    val innerBottomLeft = bottomLeft.minusHeight(width)
    val innerBottomRight = bottomRight.minusHeight(width).minusWidth(width)

    renderPolygon(
        state.renderer,
        options,
        listOf(
            topLeft,
            topRight,
            bottomRight,
            bottomLeft,
            innerBottomLeft,
            innerBottomRight,
            innerTopRight,
            innerTopLeft,
        ),
    )
}

