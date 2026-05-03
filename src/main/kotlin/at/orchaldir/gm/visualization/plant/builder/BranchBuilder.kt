package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.BranchSidePattern
import at.orchaldir.gm.core.model.ecology.plant.appearance.Branching
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoBranching
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleBranching
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.visualization.plant.PlantRenderConfig

sealed class BranchBuilder {

    fun clone() = when (this) {
        NoBranchBuilder -> NoBranchBuilder
        is SimpleBranchBuilder -> this.copy()
    }

    abstract fun processSegment(segmentEnd: Point2d, relativeLength: Factor, segmentOrientation: Orientation): List<StemData>

}

data object NoBranchBuilder : BranchBuilder() {

    override fun processSegment(segmentEnd: Point2d, relativeLength: Factor, segmentOrientation: Orientation): List<StemData> = emptyList()

}

data class SimpleBranchBuilder(
    val config: PlantRenderConfig,
    val numberGenerator: NumberGenerator,
    val branching: SimpleBranching,
    val maxLength: Distance,
    var segmentStart: Point2d,
    var relativeStart: Factor,
    var nextBranch: Factor,
    var branchingStep: Factor,
    var side: Side,
    var branchIndex: Int = 0,
) : BranchBuilder() {

    override fun processSegment(
        segmentEnd: Point2d,
        relativeLength: Factor,
        segmentOrientation: Orientation,
    ): List<StemData> {
        val branches = mutableListOf<StemData>()
        val relativeEnd = relativeStart + relativeLength

        while (nextBranch < relativeEnd && branchIndex < branching.maxCount) {
            val positionAlongSegment = (nextBranch - relativeStart) / relativeLength
            val relativePositionFromBase = (nextBranch - branching.base) / (FULL - branching.base)
            val position = segmentStart.interpolate(segmentEnd, positionAlongSegment)
            val length = maxLength * config.resolveBranchLength(branching.length, relativePositionFromBase)

            getBranchOrientation().forEach { branchOrientation ->
                val branch = buildStem(
                    config,
                    numberGenerator,
                    branching.branch,
                    position,
                    segmentOrientation - branchOrientation,
                    length,
                )

                branches.add(branch)
            }

            nextBranch += branchingStep
            branchIndex++;
        }

        segmentStart = segmentEnd
        relativeStart = relativeEnd

        return branches
    }

    fun getBranchOrientation(): List<Orientation> = when (branching.sidePattern) {
        BranchSidePattern.BothSides -> listOf(
            branching.angle.generate(numberGenerator),
            -branching.angle.generate(numberGenerator),
        )
        BranchSidePattern.AlternateSides -> when (side) {
            Side.Left -> {
                side = Side.Right

                listOf(branching.angle.generate(numberGenerator))
            }
            Side.Right -> {
                side = Side.Left

                listOf(-branching.angle.generate(numberGenerator))
            }
        }
    }

}

fun createBranchBuilder(
    config: PlantRenderConfig,
    numberGenerator: NumberGenerator,
    branching: Branching,
    start: Point2d,
    parentLength: Distance,
): BranchBuilder = when (branching) {
    NoBranching -> NoBranchBuilder
    is SimpleBranching -> SimpleBranchBuilder(
        config,
        numberGenerator,
        branching,
        parentLength * branching.maxLength,
        start,
        ZERO,
        branching.base,
        (FULL - branching.base) / branching.maxCount,
        when (branching.sidePattern) {
            BranchSidePattern.BothSides -> Side.Right
            BranchSidePattern.AlternateSides -> Side.Left //numberGenerator.select(Side.entries)
        }
    )
}