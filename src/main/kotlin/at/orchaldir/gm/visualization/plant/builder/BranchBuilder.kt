package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.Branching
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoBranching
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleBranching
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO

sealed class BranchBuilder {

    fun clone() = when (this) {
        NoBranchBuilder -> NoBranchBuilder
        is SimpleBranchBuilder -> this.copy()
    }

    abstract fun processSegment(segmentEnd: Point2d, segmentStep: Factor): List<StemData>

}

data object NoBranchBuilder : BranchBuilder() {

    override fun processSegment(segmentEnd: Point2d, segmentStep: Factor): List<StemData> = emptyList()

}

data class SimpleBranchBuilder(
    val numberGenerator: NumberGenerator,
    val branching: SimpleBranching,
    var segmentStart: Point2d,
    var currentPos: Factor,
    var branchingPos: Factor,
    var branchingStep: Factor,
) : BranchBuilder() {

    override fun processSegment(segmentEnd: Point2d, segmentStep: Factor): List<StemData> {
        val branches = mutableListOf<StemData>()
        val nextPos = currentPos + segmentStep

        while (branchingPos < nextPos) {
            branchingPos += branchingStep
        }

        segmentStart = segmentEnd
        currentPos = nextPos

        return branches
    }

}

fun createBranchBuilder(
    numberGenerator: NumberGenerator,
    branching: Branching,
    start: Point2d,
): BranchBuilder = when (branching) {
    NoBranching -> NoBranchBuilder
    is SimpleBranching -> SimpleBranchBuilder(
        numberGenerator,
        branching,
        start,
        ZERO,
        branching.base,
        (FULL - branching.base) / (branching.maxCount + 1),
    )
}