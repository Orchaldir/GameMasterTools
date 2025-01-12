package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
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

    when (data.shape) {
        CornerShape.L -> doNothing()
        CornerShape.Triangle -> {
            val length = state.aabb.convertMinSide(data.size)

            visualizeTopCornerAsTriangle(state, options, length)
            visualizeBottomCornerAsTriangle(state, options, length)
        }
    }
}
