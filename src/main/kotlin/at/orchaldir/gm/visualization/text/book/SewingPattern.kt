package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.text.book.SewingPattern
import at.orchaldir.gm.core.model.item.text.book.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.text.book.StitchType
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.renderRoundedPolygon
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeSewingPattern(
    state: TextRenderState,
    sewingPattern: SewingPattern,
) {
    when (sewingPattern) {
        is SimpleSewingPattern -> visualizeSimpleSewingPattern(state, sewingPattern)
        is ComplexSewingPattern -> visualizeComplexSewingPattern(state, sewingPattern)
    }
}

private fun visualizeSimpleSewingPattern(
    state: TextRenderState,
    simple: SimpleSewingPattern,
) {
    val color = simple.thread.getColor(state.state)
    val options = state.config.getLineOptions(color)
    val parts = simple.stitches.size
    val length = fromNumber(1.0f / parts.toFloat())
    val half = length / 2.0f
    val radius = state.aabb.convertHeight(state.config.sewingRadius.convert(simple.size))
    val sewingLength = state.config.sewingLength.convert(simple.length)
    var y = half
    val diameter = radius * 2
    val renderer = state.renderer.getLayer()

    simple.stitches.forEach { stitch ->
        when (stitch) {
            StitchType.Kettle -> {
                val start = state.aabb.getPoint(START, y)
                val hole = state.aabb.getPoint(sewingLength, y)

                val corner0 = start - radius
                val corner1 = hole.minusHeight(radius)
                val corner2 = hole.addHeight(radius)
                val corner3 = corner0.addHeight(diameter)

                renderRoundedPolygon(renderer, options, listOf(corner0, corner1, corner2, corner3))
            }

            StitchType.Empty -> doNothing()
        }

        y += length
    }
}

private fun visualizeComplexSewingPattern(
    state: TextRenderState,
    complex: ComplexSewingPattern,
) {
    val parts = complex.stitches.size
    val length = fromNumber(1.0f / parts.toFloat())
    val half = length / 2.0f
    var y = half
    val renderer = state.renderer.getLayer()

    complex.stitches.forEach { element ->
        val color = element.thread.getColor(state.state)
        val options = state.config.getLineOptions(color)
        val radius = state.aabb.convertHeight(state.config.sewingRadius.convert(element.size))
        val sewingLength = state.config.sewingLength.convert(element.length)
        val diameter = radius * 2

        when (element.stitch) {
            StitchType.Kettle -> {
                val start = state.aabb.getPoint(START, y)
                val hole = state.aabb.getPoint(sewingLength, y)

                val corner0 = start - radius
                val corner1 = hole.minusHeight(radius)
                val corner2 = hole.addHeight(radius)
                val corner3 = corner0.addHeight(diameter)

                renderRoundedPolygon(renderer, options, listOf(corner0, corner1, corner2, corner3))
            }

            StitchType.Empty -> doNothing()
        }

        y += length
    }
}
