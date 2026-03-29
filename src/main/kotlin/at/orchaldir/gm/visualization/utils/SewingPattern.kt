package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.item.common.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.common.RepeatedStitch
import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.item.common.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.common.StitchType
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
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
    0,
    Side.Right,
)

fun visualizeSewingPattern(
    state: RenderState,
    config: SewingPatternConfig,
    start: Point2d,
    end: Point2d,
    width: Distance,
    pattern: SewingPattern,
    layer: Int,
    side: Side? = null,
) = when (pattern) {
    is RepeatedStitch -> visualizeSimpleSewingPattern(
        state,
        config,
        start,
        end,
        width,
        SimpleSewingPattern(
            pattern.cord,
            pattern.thickness,
            pattern.width,
            List(pattern.count) { pattern.stitch },
        ),
        layer,
        side,
    )
    is SimpleSewingPattern -> visualizeSimpleSewingPattern(
        state,
        config,
        start,
        end,
        width,
        pattern,
        layer,
        side,
    )
    is ComplexSewingPattern -> visualizeComplexSewingPattern(
        state,
        config,
        start,
        end,
        width,
        pattern,
        layer,
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
    layer: Int,
    side: Side?,
) {
    val renderer = state.renderer().getLayer(layer)
    val options = state.getNoBorder(pattern.cord)
    val radius = width * config.sewingRadius.convert(pattern.thickness)
    val stitchWidth = width * config.sewingLength.convert(pattern.width)
    val corners = fromStartAndEnd(start, end, pattern.stitches.size).getCorners()
    var start = corners.first()

    corners
        .drop(1)
        .zip(pattern.stitches)
        .forEach { (end, stitch) ->
            visualizeStitch(
                renderer,
                options,
                stitch,
                start,
                end,
                stitchWidth,
                radius,
                side,
            )

            start = end
        }
}

private fun visualizeComplexSewingPattern(
    state: RenderState,
    config: SewingPatternConfig,
    start: Point2d,
    end: Point2d,
    width: Distance,
    pattern: ComplexSewingPattern,
    layer: Int,
    side: Side?,
) {
    val renderer = state.renderer().getLayer(layer)
    val corners = fromStartAndEnd(start, end, pattern.stitches.size).getCorners()
    var start = corners.first()

    corners
        .drop(1)
        .zip(pattern.stitches)
        .forEach { (end, complexStitch) ->
        val options = state.getNoBorder(complexStitch.cord)
        val radius = width * config.sewingRadius.convert(complexStitch.thickness)
        val stitchWidth = width * config.sewingLength.convert(complexStitch.width)

        visualizeStitch(
            renderer,
            options,
            complexStitch.stitch,
            start,
            end,
            stitchWidth,
            radius,
            side,
        )

        start = end
    }
}

private fun visualizeStitch(
    renderer: LayerRenderer,
    options: RenderOptions,
    stitch: StitchType,
    stitchStart: Point2d,
    stitchEnd: Point2d,
    width: Distance,
    radius: Distance,
    side: Side?,
) = when (stitch) {
    StitchType.Kettle -> {
        val diff = stitchEnd - stitchStart
        val normal = diff.normal()
        val center = (stitchStart + stitchEnd) / 2.0f
        val start = getOffsetForSide(normal, side, center, width)
        val end = start - normal.resize(width)

        visualizeLine(renderer, options, start, end, radius)
    }
    StitchType.Cross -> doNothing()
    StitchType.Empty -> doNothing()
}

private fun getOffsetForSide(
    normal: Point2d,
    side: Side?,
    point: Point2d,
    width: Distance,
) = when (side) {
    Side.Left -> point + normal.resize(width)
    Side.Right -> point
    null -> point + normal.resize(width / 2)
}

private fun visualizeLine(
    renderer: LayerRenderer,
    options: RenderOptions,
    start: Point2d,
    end: Point2d,
    radius: Distance,
) {
    val diff = (end - start).resize(radius)
    val normal = diff.normal()
    val corner0 = start - diff - normal
    val corner1 = end + diff - normal
    val corner2 = end + diff + normal
    val corner3 = start - diff + normal

    renderRoundedPolygon(renderer, options, listOf(corner0, corner1, corner2, corner3))
}
