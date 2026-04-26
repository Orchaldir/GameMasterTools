package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees

fun buildTrunk(
    trunk: Trunk,
    position: Point2d,
): StemData {
    val length = trunk.length.center

    return buildStem(trunk.stem, position, fromDegrees(90), length)
}

fun buildStem(
    stem: Stem,
    position: Point2d,
    orientation: Orientation,
    length: Distance,
): StemData {
    val segment = buildSegment(stem, length, position, orientation, 0)

    return StemData(
        position,
        orientation,
        stem.thickness.calculate(length, ZERO),
        segment,
    )
}

private fun buildSegment(
    stem: Stem,
    stemLength: Distance,
    start: Point2d,
    orientation: Orientation,
    index: Int,
): SegmentData {
    val end = start.createPolar(stemLength / stem.segments, orientation)
    val thickness = stem.thickness.calculate(stemLength, FULL * (index + 1) / stem.segments)
    val segments = mutableListOf<SegmentData>()

    segments.add(
        buildSegment(stem, stemLength, end, orientation, index+1)
    )

    return SegmentData(
        end,
        orientation,
        thickness,
        segments,
    )
}
