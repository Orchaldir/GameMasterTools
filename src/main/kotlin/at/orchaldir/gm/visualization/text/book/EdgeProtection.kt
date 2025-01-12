package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeEdgeProtection(
    state: TextRenderState,
    protection: EdgeProtection,
) {
    when (protection) {
        NoEdgeProtection -> doNothing()
        is ProtectedCorners -> visualizeProtectedCorners(state, protection)
    }
}

private fun visualizeProtectedCorners(
    state: TextRenderState,
    data: ProtectedCorners,
) {
    val options = FillAndBorder(data.color.toRender(), state.config.line)
    val length = state.aabb.convertMinSide(data.size)

    when (data.shape) {
        CornerShape.L -> {
            visualizeTopCornerAsL(state, options, length, length * 0.2f)
        }
        CornerShape.Triangle -> {
            visualizeTopCornerAsTriangle(state, options, length)
            visualizeBottomCornerAsTriangle(state, options, length)
        }
    }
}
