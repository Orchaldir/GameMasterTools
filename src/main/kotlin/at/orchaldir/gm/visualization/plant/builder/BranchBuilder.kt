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
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.visualization.plant.PlantRenderConfig

sealed class BranchBuilder {

    fun clone() = when (this) {
        NoBranchBuilder -> NoBranchBuilder
        is SimpleBranchBuilder -> this.copy(processor = processor.copy())
    }

    abstract fun processSegment(
        segmentEnd: Point2d,
        relativeLength: Factor,
        segmentOrientation: Orientation,
    ): List<StemData>

}

data object NoBranchBuilder : BranchBuilder() {

    override fun processSegment(
        segmentEnd: Point2d,
        relativeLength: Factor,
        segmentOrientation: Orientation,
    ): List<StemData> = emptyList()

}

data class SimpleBranchBuilder(
    val config: PlantRenderConfig,
    val numberGenerator: NumberGenerator,
    val branching: SimpleBranching,
    val processor: StemProcessor,
    val maxLength: Distance,
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
        processor.startSegment(segmentEnd, relativeLength)

        while (nextBranch < processor.relativeEnd && branchIndex < branching.maxCount) {
            val position = processor.calculatePositionAlongSegment(nextBranch)
            val relativePositionFromBase = processor.calculateRelativePositionFromBase(nextBranch)
            val length = maxLength * config.resolveBranchLength(branching.length, relativePositionFromBase)

            getBranchOrientation().forEach { (branchSide, branchOrientation) ->
                val branch = buildStem(
                    config,
                    numberGenerator,
                    branching.branch,
                    position,
                    segmentOrientation - branchOrientation,
                    branchSide,
                    length,
                )

                branches.add(branch)
            }

            nextBranch += branchingStep
            branchIndex++
        }

        processor.endSegment()

        return branches
    }

    fun getBranchOrientation(): List<Pair<Side,Orientation>> = when (branching.sidePattern) {
        BranchSidePattern.BothSides -> listOf(
            Pair(Side.Left, branching.angle.generate(numberGenerator)),
            Pair(Side.Right, -branching.angle.generate(numberGenerator)),
        )

        BranchSidePattern.AlternateSides -> {
            val orientation = when (side) {
                Side.Left -> branching.angle.generate(numberGenerator)
                Side.Right -> -branching.angle.generate(numberGenerator)
            }
            val result = Pair(side, orientation)

            side = side.flip()

            listOf(result)
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
    is SimpleBranching -> {
        val branchingStep = (FULL - branching.base) / branching.maxCount

        SimpleBranchBuilder(
            config,
            numberGenerator,
            branching,
            StemProcessor(
                start,
                branching.base,
                FULL - branchingStep
            ),
            parentLength * branching.maxLength,
            branching.base,
            branchingStep,
            when (branching.sidePattern) {
                BranchSidePattern.BothSides -> Side.Right
                BranchSidePattern.AlternateSides -> Side.Left //numberGenerator.select(Side.entries)
            }
        )
    }
}