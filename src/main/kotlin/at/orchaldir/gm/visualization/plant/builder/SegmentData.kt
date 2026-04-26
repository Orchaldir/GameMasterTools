package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance

data class SegmentData(
    val end: Point2d,
    val thickness: Distance,
    val next: List<SegmentData>,
)
