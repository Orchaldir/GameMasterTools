package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
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
        "stem-shapes.svg",
        PLANT_CONFIG,
        listOf(
            listOf(
                createTree(StraightStem),
                createTree(curvedStem),
                createTree(StraightStem, baseSplitting),
                createTree(StraightStem, segmentSplitting),
            )
        ),
    )
}

fun createTree(shape: StemShape, splitting: StemSplitting = NoStemSplitting) = Tree(
    Trunk(
        Distribution(Distance.fromMeters(1)),
        Stem(
            3,
            shape,
            LinearStemThickness(fromPercentage(5)),
            splitting,
        ),
        Color.SaddleBrown,
    )
)

