package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance

data class StemData(
    val start: Point2d,
    val thickness: Distance,
    val segment: SegmentData,
)
