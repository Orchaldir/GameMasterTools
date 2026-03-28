package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.item.common.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.item.common.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.common.StitchType
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.SegmentSplitter.Companion.fromStartAndEnd
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions
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
) = visualizeSewingPattern(
    state,
    state.config.sewing,
    state.aabb.getPoint(START, START),
    state.aabb.getPoint(START, END),
    state.aabb.size.width,
    pattern,
    Side.Right,
)

fun visualizeSewingPattern(
    state: RenderState,
    config: SewingPatternConfig,
    start: Point2d,
    end: Point2d,
    width: Distance,
    pattern: SewingPattern,
    side: Side? = null,
) = when (pattern) {
    is SimpleSewingPattern -> visualizeSimpleSewingPattern(
        state,
        config,
        start,
        end,
        width,
        pattern,
        side,
    )
    is ComplexSewingPattern -> visualizeComplexSewingPattern(
        state,
        config,
        start,
        end,
        width,
        pattern,
        side,
    )
}

private fun visualizeSimpleSewingPattern(
    state: RenderState,
    config: SewingPatternConfig,
    start: Point2d,
    end: Point2d,
    width: Distance,
    pattern: SimpleSewingPattern,
    side: Side?,
) {
    val renderer = state.renderer().getLayer()
    val options = state.getFillAndBorder(pattern.thread)
    val radius = width * config.sewingRadius.convert(pattern.size)
    val lengthFactor = config.sewingLength.convert(pattern.length)
    val length = width * lengthFactor
    val splitter = fromStartAndEnd(start, end, pattern.stitches.size)

    splitter.getCenters()
        .zip(pattern.stitches)
        .forEach { (center, stitch) ->
            visualizeStitch(renderer, options, stitch, center, length, radius, side)
        }
}

private fun visualizeComplexSewingPattern(
    state: RenderState,
    config: SewingPatternConfig,
    start: Point2d,
    end: Point2d,
    width: Distance,
    pattern: ComplexSewingPattern,
    side: Side?,
) {
    val renderer = state.renderer().getLayer()
    val splitter = fromStartAndEnd(start, end, pattern.stitches.size)

    splitter.getCenters()
        .zip(pattern.stitches)
        .forEach { (center, complexStitch) ->

        val options = state.getFillAndBorder(complexStitch.thread)
        val radius = width * config.sewingRadius.convert(complexStitch.size)
        val lengthFactor = config.sewingLength.convert(complexStitch.length)
        val length = width * lengthFactor

        visualizeStitch(renderer, options, complexStitch.stitch, center, length, radius, side)
    }
}

private fun visualizeStitch(
    renderer: LayerRenderer,
    options: RenderOptions,
    stitch: StitchType,
    center: Point2d,
    length: Distance,
    radius: Distance,
    side: Side?,
) = when (stitch) {
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
        val corner3 = corner0.addHeight(radius * 2)

        renderRoundedPolygon(renderer, options, listOf(corner0, corner1, corner2, corner3))
    }

    StitchType.Empty -> doNothing()
}
