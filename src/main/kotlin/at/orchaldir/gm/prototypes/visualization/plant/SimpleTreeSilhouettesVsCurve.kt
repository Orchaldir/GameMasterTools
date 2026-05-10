package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.CurvedStem
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemShape
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemThicknessType
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouetteShape
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.TEN_PERCENTS
import at.orchaldir.gm.utils.math.THIRD
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
