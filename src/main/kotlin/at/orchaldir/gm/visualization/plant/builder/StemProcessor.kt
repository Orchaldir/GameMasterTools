package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Orientation

data class StemProcessor(
    var segmentStart: Point2d,
    var segmentEnd: Point2d,
    var relativeStart: Factor,
    var relativeEnd: Factor,
    var relativeLength: Factor,
    var baseStart: Factor,
    var baseEnd: Factor,
) {
    constructor(
        segmentStart: Point2d,
        baseStart: Factor = START,
        baseEnd: Factor = FULL,
    ): this(
        segmentStart,
        segmentStart,
        ZERO,
        ZERO,
        ZERO,
        baseStart,
        baseEnd,
    )

    fun startSegment(
        end: Point2d,
        length: Factor,
        segmentOrientation: Orientation,
    ) {
        segmentEnd = end
        relativeEnd = relativeStart + length
        relativeLength = length
    }

    fun endSegment() {
        segmentStart = segmentEnd
        relativeStart = relativeEnd
    }

    fun calculateRelativePositionAlongSegment(
        positionAlongStem: Factor,
    ) = (positionAlongStem - relativeStart) / relativeLength

    fun calculatePositionAlongSegment(
        positionAlongStem: Factor,
    ) = segmentStart
        .interpolate(segmentEnd, calculateRelativePositionAlongSegment(positionAlongStem))

    fun calculateRelativePositionFromBase(
        positionAlongStem: Factor,
    ) = (positionAlongStem - baseStart) / (baseEnd - baseStart)
}
