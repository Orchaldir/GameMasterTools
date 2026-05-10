package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance

data class StemData(
    val start: Point2d,
    val thickness: Distance,
    val hasRoundedEnd: Boolean,
    val segment: SegmentData,
    val length: Distance,
) {
    fun getBranches(): List<StemData> {
        val branches = mutableListOf<StemData>()

        segment.getBranches(branches)

        return branches
    }
}
