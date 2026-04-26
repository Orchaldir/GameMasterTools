package at.orchaldir.gm.visualization.plant.visualization

import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.visualization.plant.builder.SegmentData
import at.orchaldir.gm.visualization.plant.builder.StemData

fun createStemPolygon(
    stem: StemData,
): Polygon2d {
    val builder = Polygon2dBuilder()

    builder.addLeftAndRightPoint(stem.start, stem.segment.orientation, stem.thickness / 2)

    addSegment(builder, stem.segment)

    return builder.build()
}

private fun addSegment(
    builder: Polygon2dBuilder,
    segment: SegmentData,
) {
    if (segment.next.isEmpty()) {
        builder.addLeftAndRightPoint(segment.end, segment.orientation, segment.thickness / 2)
    } else if (segment.next.size == 1) {
        builder.addLeftAndRightPoint(segment.end, segment.orientation, segment.thickness / 2)

        addSegment(builder, segment.next[0])
    }
}
