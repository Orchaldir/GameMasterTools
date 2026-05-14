package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.*
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.unit.Orientation

fun main() {

    renderPlantTable(
        State(),
        "simple-tree-silhouettes-curved.svg",
        PLANT_CONFIG,
        listOf(10L, 20L, 30L).map { degrees ->
            val angle = Orientation.fromDegrees(degrees)
            Pair(angle.toString(), CurvedStem(angle))
        },
        addNames(TreeSilhouetteShape.entries),
        ::createSilhouette,
    )
}

private fun createSilhouette(silhouetteShape: TreeSilhouetteShape, stemShape: StemShape): PlantAppearance = Tree(
    Trunk(stem = Stem(shape = stemShape)),
    SimpleTreeSilhouette(silhouetteShape),
)
