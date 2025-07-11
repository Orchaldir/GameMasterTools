package at.orchaldir.gm.visualization.utils

import at.orchaldir.gm.core.model.util.part.Segment
import at.orchaldir.gm.core.model.util.part.SegmentShape
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.RenderState

fun visualizeSegments(
    state: RenderState,
    segments: Segments,
    start: Point2d,
    isUp: Boolean,
    baseLength: Distance,
    baseDiameter: Distance,
) {
    val renderer = state.renderer().getLayer()
    var current = start

    segments.segments.forEach { segment ->
        val color = segment.main.getColor(state.state())
        val options = FillAndBorder(color.toRender(), state.lineOptions())
        val segmentLength = segment.calculateLength(baseLength)
        val segmentDiameter = segment.calculateDiameter(baseDiameter)
        val segmentSize = Size2d(segmentDiameter, segmentLength)
        val half = segmentLength / 2
        val step = if (isUp) {
            -half
        } else {
            half
        }
        val center = current.addHeight(step)
        val aabb = AABB.fromCenter(center, segmentSize)

        visualizeSegment(renderer, options, aabb, isUp, segment)

        current = center.addHeight(step)
    }
}

fun visualizeSegment(
    renderer: LayerRenderer,
    options: FillAndBorder,
    aabb: AABB,
    isUp: Boolean,
    segment: Segment,
) = when (segment.shape) {
    SegmentShape.Cone -> {
        val builder = Polygon2dBuilder()

        if (isUp) {
            builder.addMirroredPoints(aabb, FULL, END)
                .addLeftPoint(aabb, CENTER, START)
        } else {
            builder.addMirroredPoints(aabb, FULL, START)
                .addLeftPoint(aabb, CENTER, END)
        }

        renderer.renderPolygon(builder.build(), options)
    }

    SegmentShape.Cylinder -> renderer.renderRectangle(aabb, options)
    SegmentShape.RoundedCylinder -> renderer.renderRoundedPolygon(Polygon2d(aabb.getCorners()), options)
    SegmentShape.Sphere -> renderer.renderCircle(aabb.getCenter(), aabb.size.height / 2, options)
    SegmentShape.Ellipse -> renderer.renderEllipse(aabb, options)
}