package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation

data class SegmentData(
    val end: Point2d,
    val orientation: Orientation,
    val thickness: Distance,
    val next: List<SegmentData>,
    val branches: List<StemData>,
) {
    fun getBranches(total: MutableList<StemData>) {
        total.addAll(branches)

        next.forEach {
            it.getBranches(total)
        }
    }
}
