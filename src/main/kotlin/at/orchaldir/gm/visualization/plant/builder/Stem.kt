package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees

fun buildTrunk(
    numberGenerator: NumberGenerator,
    trunk: Trunk,
    position: Point2d,
): StemData {
    val height = trunk.height.center

    return buildStem(numberGenerator, trunk.stem, position, fromDegrees(-90), height)
}

fun buildStem(
    numberGenerator: NumberGenerator,
    stem: Stem,
    position: Point2d,
    orientation: Orientation,
    length: Distance,
): StemData {
    val branchBuilder = createBranchBuilder(numberGenerator, stem.branching, position, length)
    val segment = buildSegment(
        numberGenerator,
        stem,
        branchBuilder,
        length,
        position,
        orientation,
        0,
    )

    return StemData(
        position,
        stem.thickness.calculate(length, ZERO),
        stem.thickness.hasRoundedEnd(),
        segment,
    )
}

private fun buildSegment(
    numberGenerator: NumberGenerator,
    stem: Stem,
    branchBuilder: BranchBuilder,
    stemLength: Distance,
    start: Point2d,
    orientation: Orientation,
    index: Int,
): SegmentData {
    val end = start.createPolar(stemLength / stem.segments, orientation)
    val nextIndex = index + 1
    val thickness = stem.thickness.calculate(stemLength, FULL * nextIndex / stem.segments)
    val branches = branchBuilder.processSegment(end, FULL / stem.segments, orientation)
    val segments = mutableListOf<SegmentData>()

    if (nextIndex < stem.segments) {
        val endOrientation = stem.shape.calculate(numberGenerator, orientation, stem.segments)

        stem.splitting
            .calculateSplits(numberGenerator, endOrientation, index)
            .forEach { splitOrientation ->
                segments.add(
                    buildSegment(
                        numberGenerator,
                        stem,
                        branchBuilder.clone(),
                        stemLength,
                        end,
                        splitOrientation,
                        nextIndex,
                    )
                )
            }
    }

    return SegmentData(
        end,
        orientation,
        thickness,
        segments,
        branches,
    )
}
