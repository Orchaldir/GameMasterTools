package at.orchaldir.gm.visualization.book.book

import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.book.BookRenderState
import at.orchaldir.gm.visualization.renderRoundedPolygon

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
    val x = half * 0.5f
    val radius = state.aabb.convertHeight(state.config.sewingSize.convert(simple.size))
    val diameter = radius * 2
    val renderer = state.renderer.getLayer()

    repeat(parts) {
        val start = state.aabb.getPoint(START, y)
        val hole = state.aabb.getPoint(x, y)

        val corner0 = start - radius
        val corner1 = hole.minusHeight(radius)
        val corner2 = hole.addHeight(radius)
        val corner3 = corner0.addHeight(diameter)

        renderRoundedPolygon(renderer, options, listOf(corner0, corner1, corner2, corner3))

        y += length
    }
}
