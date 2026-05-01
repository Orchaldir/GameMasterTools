package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distribution
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees

fun main() {
    renderPlantTable(
        State(),
        "stem-thicknesses.svg",
        PLANT_CONFIG,
        addNames(StemThicknessType.entries),
        listOf(
            Pair("Rounded", true),
            Pair("Sharp", false),
        ),
        ::createTree,
    )
}

private fun createTree(hasRoundedEnd: Boolean, type: StemThicknessType) = Tree(
    Trunk(
        Distribution(Distance.fromMeters(1)),
        Stem(
            3,
            CurvedStem(Variance(fromDegrees(30))),
            when (type) {
                StemThicknessType.Constant -> ConstantStemThickness(fromPercentage(5), hasRoundedEnd)
                StemThicknessType.Linear -> LinearStemThickness(fromPercentage(5), hasRoundedEnd = hasRoundedEnd)
            },
            NoStemSplitting,
        ),
        Color.SaddleBrown,
    )
)

