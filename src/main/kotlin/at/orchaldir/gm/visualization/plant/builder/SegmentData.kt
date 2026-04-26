package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.Point2d

data class SegmentData(
    val end: Point2d,
    val next: List<SegmentData>,
)
