package at.orchaldir.gm.visualization.plant.builder

import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.UndefinedPlantAppearance
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.visualization.plant.PlantRenderConfig


sealed class PlantData

data class TreeData(
    val trunk: StemData,
    val bark: Color,
) : PlantData()

data object UndefinedPlantData : PlantData()

fun buildPlant(
    config: PlantRenderConfig,
    numberGenerator: NumberGenerator,
    plant: PlantAppearance,
    position: Point2d = Point2d(),
): PlantData = when (plant) {
    is Tree -> TreeData(
        buildTrunk(config, numberGenerator, plant.trunk, position),
        plant.trunk.bark,
    )

    UndefinedPlantAppearance -> UndefinedPlantData
}