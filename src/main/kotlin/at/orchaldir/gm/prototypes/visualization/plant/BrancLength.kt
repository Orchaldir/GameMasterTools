package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees

fun main() {
    val curvedStem = CurvedStem(Variance(fromDegrees(20), fromDegrees(20)))
    val baseSplitting = BaseSplitting(FULL, Variance(fromDegrees(30)))
    val segmentSplitting = SegmentSplitting(THREE_QUARTER, Variance(fromDegrees(30)))

    renderPlantTable(
        State(),
        "branch-length.svg",
        PLANT_CONFIG,
        addNames(BranchLength.entries),
        listOf(
            Pair("Straight", Pair(StraightStem, NoStemSplitting)),
            Pair("Curved", Pair(curvedStem, NoStemSplitting)),
            Pair("Base Splitting", Pair(StraightStem, baseSplitting)),
            Pair("Segment Splitting", Pair(StraightStem, segmentSplitting)),
        ),
        ::createTree,
    )
}

private fun createTree(pair: Pair<StemShape,StemSplitting>, length: BranchLength) = Tree(
    Trunk(
        Distribution(Distance.fromMeters(1)),
        Stem(
            3,
            pair.first,
            LinearStemThickness(fromPercentage(5)),
            pair.second,
            SimpleBranching(
                10,
                BranchSidePattern.BothSides,
                HALF,
                QUARTER,
                length,
            )
        ),
        Color.SaddleBrown,
    )
)
