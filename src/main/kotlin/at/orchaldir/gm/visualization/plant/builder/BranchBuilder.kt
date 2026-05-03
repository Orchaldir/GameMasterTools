package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.BranchLength
import at.orchaldir.gm.core.model.ecology.plant.appearance.BranchSidePattern
import at.orchaldir.gm.core.model.ecology.plant.appearance.Branching
import at.orchaldir.gm.core.model.ecology.plant.appearance.BranchingType
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoBranching
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleBranching
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class BranchBuilder {

    fun clone() = when (this) {
        NoBranchBuilder -> NoBranchBuilder
        is SimpleBranchBuilder -> this.copy()
    }

    abstract fun processSegment(segmentEnd: Point2d): List<StemData>

}

data object NoBranchBuilder : BranchBuilder() {

    override fun processSegment(segmentEnd: Point2d): List<StemData> = emptyList()

}

data class SimpleBranchBuilder(
    val numberGenerator: NumberGenerator,
    val branching: SimpleBranching,
    var segmentStart: Point2d,
    var relativePosition: Factor = ZERO,
) : BranchBuilder() {

    override fun processSegment(segmentEnd: Point2d): List<StemData> {
        val branches = mutableListOf<StemData>()

        segmentStart = segmentEnd

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
    )
}