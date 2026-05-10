package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.appearance.NoTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.SimpleTreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.Stem
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouette
import at.orchaldir.gm.core.model.ecology.plant.appearance.Trunk
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.visualization.plant.PlantRenderConfig

data class SilhouetteData(
    val color: Color,
    val silhouette: Polygon2d,
)

fun buildTreeSilhouette(
    config: PlantRenderConfig,
    numberGenerator: NumberGenerator,
    silhouette: TreeSilhouette,
    trunk: StemData,
): List<SilhouetteData> = when (silhouette) {
    NoTreeSilhouette -> emptyList()
    is SimpleTreeSilhouette -> buildSimpleTreeSilhouette(
        config,
        numberGenerator,
        silhouette,
        trunk,
    )
}

private fun buildSimpleTreeSilhouette(
    config: PlantRenderConfig,
    numberGenerator: NumberGenerator,
    silhouette: SimpleTreeSilhouette,
    trunk: StemData,
): List<SilhouetteData> {
    return emptyList()
}