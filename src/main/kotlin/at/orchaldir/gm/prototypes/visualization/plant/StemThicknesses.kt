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
    renderPlantTable(
        State(),
        "stem-thicknesses.svg",
        PLANT_CONFIG,
        listOf(
            listOf(
                createTree(ConstantStemThickness(fromPercentage(5))),
                createTree(LinearStemThickness(fromPercentage(5))),
            )
        ),
    )
}

private fun createTree(thickness: StemThickness) = Tree(
    Trunk(
        Distribution(Distance.fromMeters(1)),
        Stem(
            3,
            CurvedStem(Variance(fromDegrees(30))),
            thickness,
            NoStemSplitting,
        ),
        Color.SaddleBrown,
    )
)

