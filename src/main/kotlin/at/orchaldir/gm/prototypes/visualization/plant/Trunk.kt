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
import at.orchaldir.gm.core.model.ecology.plant.appearance.StraightStem
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import io.ktor.http.invoke

fun main() {
    val height = Distance.fromMeters(1)
    val thickness = LinearStemThickness(fromPercentage(5))
    val straightTrunk = Trunk(
        Distribution(height),
        Stem(
            3,
            StraightStem,
            thickness,
        ),
        Color.SaddleBrown,
    )
    val curvedTrunk = Trunk(
        Distribution(height),
        Stem(
            3,
            CurvedStem(Variance(fromDegrees(20), fromDegrees(20))),
            thickness,
        ),
        Color.SaddleBrown,
    )
    val baseSplit = Trunk(
        Distribution(height),
        Stem(
            3,
            StraightStem,
            thickness,
            BaseSplitting(FULL, Variance(fromDegrees(30))),
        ),
        Color.SaddleBrown,
    )

    renderPlantTable(
        State(),
        "tree-trunk.svg",
        PLANT_CONFIG,
        listOf(listOf(
            Tree(straightTrunk),
            Tree(curvedTrunk),
            Tree(baseSplit),
        )),
    )
}

