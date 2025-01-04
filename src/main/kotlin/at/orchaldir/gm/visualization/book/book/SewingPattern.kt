package at.orchaldir.gm.visualization.book.book

import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.book.BookRenderState

fun visualizeSewingPattern(
    state: BookRenderState,
    sewingPattern: SewingPattern,
) {
    when (sewingPattern) {
        is SimpleSewingPattern -> visualizeSimpleSewingPattern(state, sewingPattern)
        is ComplexSewingPattern -> TODO()
    }
}

private fun visualizeSimpleSewingPattern(
    state: BookRenderState,
    simple: SimpleSewingPattern,
) {
    val options = FillAndBorder(simple.color.toRender(), state.config.line)
    val parts = simple.stitches.size
    val length = Factor(1.0f / parts.toFloat())
    val half = length / 2.0f
    var y = half
    val x = half * state.config.sewingSize.convert(simple.size)
    val radius = state.aabb.convertHeight(x / 2.0f)

    repeat(parts) {
        val hole = state.aabb.getPoint(x, y)

        state.renderer.getLayer().renderCircle(hole, radius, options)

        y += length
    }
}
