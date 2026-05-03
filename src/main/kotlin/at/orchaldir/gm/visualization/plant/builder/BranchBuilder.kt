package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.Branching
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoBranching
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleBranching
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation

sealed class BranchBuilder {

    fun clone() = when (this) {
        NoBranchBuilder -> NoBranchBuilder
        is SimpleBranchBuilder -> this.copy()
    }

    abstract fun processSegment(segmentEnd: Point2d, relativeLength: Factor, orientation: Orientation): List<StemData>

}

data object NoBranchBuilder : BranchBuilder() {

    override fun processSegment(segmentEnd: Point2d, relativeLength: Factor, orientation: Orientation): List<StemData> = emptyList()

}

data class SimpleBranchBuilder(
    val numberGenerator: NumberGenerator,
    val branching: SimpleBranching,
    val maxLength: Distance,
    var segmentStart: Point2d,
    var relativeStart: Factor,
    var nextBranch: Factor,
    var branchingStep: Factor,
) : BranchBuilder() {

    override fun processSegment(
        segmentEnd: Point2d,
        relativeLength: Factor,
        orientation: Orientation,
    ): List<StemData> {
        val branches = mutableListOf<StemData>()
        val relativeEnd = relativeStart + relativeLength

        while (nextBranch < relativeEnd) {
            val positionAlongSegment = (nextBranch - relativeStart) / relativeLength
            val branch = buildStem(
                numberGenerator,
                branching.branch,
                segmentStart.interpolate(segmentEnd, positionAlongSegment),
                orientation - branching.angle.generate(numberGenerator),
                maxLength,
            )

            branches.add(branch)

            nextBranch += branchingStep
        }

        segmentStart = segmentEnd
        relativeStart = relativeEnd

        return branches
    }

}

fun createBranchBuilder(
    numberGenerator: NumberGenerator,
    branching: Branching,
    start: Point2d,
    parentLength: Distance,
): BranchBuilder = when (branching) {
    NoBranching -> NoBranchBuilder
    is SimpleBranching -> SimpleBranchBuilder(
        numberGenerator,
        branching,
        parentLength * branching.maxLength,
        start,
        ZERO,
        branching.base,
        (FULL - branching.base) / (branching.maxCount + 0.9f),
    )
}