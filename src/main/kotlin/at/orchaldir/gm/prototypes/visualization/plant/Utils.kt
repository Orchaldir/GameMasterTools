package at.orchaldir.gm.prototypes.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.renderTable
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.plant.PlantRenderConfig
import at.orchaldir.gm.visualization.plant.PlantRenderState
import at.orchaldir.gm.visualization.plant.builder.PlantData
import at.orchaldir.gm.visualization.plant.builder.buildPlant
import at.orchaldir.gm.visualization.plant.calculateSize
import at.orchaldir.gm.visualization.plant.visualization.visualizePlant
import kotlin.random.Random

private val MIN_SIZE = Size2d.square(fromCentimeters(1))

val PLANT_CONFIG = PlantRenderConfig(
    LineOptions(Color.Black.toRender(), fromMicrometers(200)),
    fromPercentage(200),
)

fun renderPlantTable(
    state: State,
    filename: String,
    config: PlantRenderConfig,
    plants: List<List<PlantAppearance>>,
) {
    val numberGenerator = RandomNumberGenerator(Random(System.currentTimeMillis()))
    val dataMap = mutableMapOf<PlantAppearance, PlantData>()
    val size = plants.fold(MIN_SIZE) { rowSize, list ->
        list.fold(rowSize) { columnSize, plant ->
            val data = buildPlant(numberGenerator, plant)
            val size = calculateSize(config, data) ?: MIN_SIZE
            dataMap[plant] = data

            columnSize.max(size)
        }
    }

    renderTable(filename, size, plants) { renderAabb, renderer, plant ->
        val data = dataMap[plant]!!
        val renderState = PlantRenderState(
            state,
            renderAabb,
            PLANT_CONFIG,
            renderer,
        )

        visualizePlant(renderState, data, renderAabb.getPoint(HALF, END))
    }
}