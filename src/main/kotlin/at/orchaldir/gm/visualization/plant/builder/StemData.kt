package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation

data class StemData(
    val start: Point2d,
    val orientation: Orientation,
    val thickness: Distance,
    val first: SegmentData,
)
