package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.item.common.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.item.common.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.common.StitchType
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.SegmentSplitter.Companion.fromStartAndEnd
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.renderRoundedPolygon
import at.orchaldir.gm.visualization.text.TextRenderState

data class SewingPatternConfig(
    val sewingRadius: SizeConfig<Factor>,
    val sewingLength: SizeConfig<Factor>,
)

fun visualizeSewingPattern(
    state: TextRenderState,
    pattern: SewingPattern,
)  = visualizeSewingPattern( state, state.config.sewing, pattern)

fun visualizeSewingPattern(
    state: TextRenderState,
    config: SewingPatternConfig,
    pattern: SewingPattern,
) {
    when (pattern) {
        is SimpleSewingPattern -> visualizeSimpleSewingPattern(state, config, pattern)
        is ComplexSewingPattern -> visualizeComplexSewingPattern(state, config, pattern)
    }
}

private fun visualizeSimpleSewingPattern(
    state: TextRenderState,
    config: SewingPatternConfig,
    simple: SimpleSewingPattern,
) {
    val options = state.getFillAndBorder(simple.thread)
    val parts = simple.stitches.size
    val length = fromNumber(1.0f / parts.toFloat())
    val half = length / 2.0f
    val radius = state.aabb.convertHeight(config.sewingRadius.convert(simple.size))
    val sewingLength = config.sewingLength.convert(simple.length)
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
    state: RenderState,
    config: SewingPatternConfig,
    start: Point2d,
    end: Point2d,
    width: Distance,
    complex: ComplexSewingPattern,
    side: Side?,
) {
    val renderer = state.renderer().getLayer()
    val splitter = fromStartAndEnd(start, end, complex.stitches.size)
    val centers = splitter.getCenters()

    centers
        .zip(complex.stitches)
        .forEach { (center, stitch) ->

        val options = state.getFillAndBorder(stitch.thread)
        val radius = width * config.sewingRadius.convert(stitch.size)
        val lengthFactor = config.sewingLength.convert(stitch.length)
        val length = width * lengthFactor
        val diameter = radius * 2

        when (stitch.stitch) {
            StitchType.Kettle -> {
                val start = when (side) {
                    Side.Left -> center.minus(length)
                    Side.Right -> center
                    null -> center.minus(length / 2)
                }
                val hole = start.addWidth(length)

                val corner0 = start - radius
                val corner1 = hole.minusHeight(radius)
                val corner2 = hole.addHeight(radius)
                val corner3 = corner0.addHeight(diameter)

                renderRoundedPolygon(renderer, options, listOf(corner0, corner1, corner2, corner3))
            }

            StitchType.Empty -> doNothing()
        }
    }
}
