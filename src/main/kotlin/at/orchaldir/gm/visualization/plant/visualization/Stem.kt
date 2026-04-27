package at.orchaldir.gm.visualization.plant.visualization

import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.plant.builder.SegmentData
import at.orchaldir.gm.visualization.plant.builder.StemData

fun createStemPolygon(
    stem: StemData,
): Polygon2d {
    val builder = Polygon2dBuilder()

    builder.addLeftAndRightPoint(stem.start, stem.segment.orientation, stem.thickness / 2, true)

    addSegment(builder, stem.segment)

    return builder.build()
}

private fun addSegment(
    builder: Polygon2dBuilder,
    segment: SegmentData,
) {
    if (segment.next.isEmpty()) {
        val (left, right) = segment.end.createLeftAndRightPoint(segment.orientation, segment.thickness / 2)

        builder.addPoint(left)
        builder.addPoint(right)
    } else if (segment.next.size == 1) {
        val next = segment.next[0]
        val before = segment.end.createPolar(-segment.thickness, segment.orientation)
        val after = segment.end.createPolar(segment.thickness, next.orientation)
        val (beforeLeft, beforeRight) = before.createLeftAndRightPoint(segment.orientation, segment.thickness / 2)
        val (afterLeft, afterRight) = after.createLeftAndRightPoint(next.orientation, segment.thickness / 2)


        builder.addPoint(beforeLeft)
        builder.addPoint(afterLeft)

        addSegment(builder, next)

        builder.addPoint(afterRight)
        builder.addPoint(beforeRight)
    } else if (segment.next.size == 2) {
        val next0 = segment.next[0]
        val next1 = segment.next[1]
        val before = segment.end.createPolar(-segment.thickness, segment.orientation)
        val after0 = segment.end.createPolar(segment.thickness, next0.orientation)
        val after1 = segment.end.createPolar(segment.thickness, next1.orientation)
        val (beforeLeft, beforeRight) = before.createLeftAndRightPoint(segment.orientation, segment.thickness / 2)
        val (afterLeft0, afterRight0) = after0.createLeftAndRightPoint(next0.orientation, segment.thickness / 2)
        val (afterLeft1, afterRight1) = after1.createLeftAndRightPoint(next1.orientation, segment.thickness / 2)

        builder.addPoint(beforeLeft)
        builder.addPoint(afterLeft0)

        addSegment(builder, next0)

        builder.addPoint(afterRight0.calculateMiddle(afterLeft1))

        addSegment(builder, next1)

        builder.addPoint(afterRight1)
        builder.addPoint(beforeRight)
    }
}
