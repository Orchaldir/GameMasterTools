package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.LinearStemThickness
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.appearance.BaseSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.CurvedStem
import at.orchaldir.gm.core.model.ecology.plant.appearance.NoStemSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.SegmentSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemShape
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemSplitting
import at.orchaldir.gm.core.model.ecology.plant.appearance.StraightStem
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees

fun main() {
    val baseSplitting = BaseSplitting(FULL, Variance(fromDegrees(30)))
    val segmentSplitting = SegmentSplitting(THREE_QUARTER, Variance(fromDegrees(30)))

    renderPlantTable(
        State(),
        "tree-trunk.svg",
        PLANT_CONFIG,
        listOf(listOf(
            createTree(StraightStem),
            createTree(CurvedStem(Variance(fromDegrees(20), fromDegrees(20)))),
            createTree(StraightStem, baseSplitting),
            createTree(StraightStem, segmentSplitting),
        )),
    )
}

fun createTree(shape: StemShape, splitting: StemSplitting = NoStemSplitting) = Tree(Trunk(
    Distribution(Distance.fromMeters(1)),
    Stem(
        3,
        shape,
        LinearStemThickness(fromPercentage(5)),
        splitting,
    ),
    Color.SaddleBrown,
))

