package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
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
    protectedCorners: ProtectedCorners,
) {

}
