package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.StemThicknessType
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouetteShape
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.TEN_PERCENTS
import at.orchaldir.gm.utils.math.THIRD

fun main() {
    renderPlantTable(
        State(),
        "simple-tree-silhouettes.svg",
        PLANT_CONFIG,
        listOf(
            createSilhouette("Rounded", THIRD, THIRD),
            createSilhouette("Sharp", TEN_PERCENTS, HALF),
        ),
        addNames(TreeSilhouetteShape.entries),
        ::createSilhouette,
    )
}

private fun createSilhouette(name: String, base: Factor, width: Factor) =
    Pair(name, SimpleTreeSilhouette(base = base, width = width))

private fun createSilhouette(shape: TreeSilhouetteShape, silhouette: SimpleTreeSilhouette): PlantAppearance = Tree(
    silhouette = silhouette.copy(shape = shape)
)
