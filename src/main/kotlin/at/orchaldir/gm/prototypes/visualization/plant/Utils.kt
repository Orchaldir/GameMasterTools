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
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.plant.PlantRenderConfig
import at.orchaldir.gm.visualization.plant.PlantRenderState
import at.orchaldir.gm.visualization.plant.builder.PlantData
import at.orchaldir.gm.visualization.plant.builder.buildPlant
import at.orchaldir.gm.visualization.plant.calculateSize
import at.orchaldir.gm.visualization.plant.visualization.visualizePlant
import kotlin.random.Random

private val MIN_SIZE = Size2d.square(fromCentimeters(1))

val PLANT_CONFIG = PlantRenderConfig(
    LineOptions(Color.Black.toRender(), fromMillimeters(5)),
    fromPercentage(20),
    fromPercentage(20),
    2,
    fromPercentage(20),
    fromPercentage(40),
)

fun <C, R> renderPlantTable(
    state: State,
    filename: String,
    config: PlantRenderConfig,
    rows: List<Pair<String, R>>,
    columns: List<Pair<String, C>>,
    create: (C, R) -> PlantAppearance,
) {
    val numberGenerator = RandomNumberGenerator(Random(System.currentTimeMillis()))
    val dataMap = mutableMapOf<Pair<R, C>, Pair<PlantData, PaddedSize>>()

    renderTable(
        filename,
        rows,
        columns,
        MIN_SIZE,
        false,
        { column, row ->
            val plant = create(column, row)
            val data = buildPlant(config, numberGenerator, plant)
            val size = calculateSize(config, data) ?: PaddedSize(MIN_SIZE)
            dataMap[Pair(row, column)] = Pair(data, size)

            size.getFullSize()
        },
        { renderAabb, renderer, renderFront, column, row ->
            val (data, paddedSize) = dataMap.getValue(Pair(row, column))
            val innerAabb = paddedSize.getInnerAABB(renderAabb)
            val renderState = PlantRenderState(
                state,
                PLANT_CONFIG,
                renderer,
            )

            visualizePlant(renderState, data, innerAabb.getPoint(HALF, END))
        },
    )
}

fun renderPlantTable(
    state: State,
    filename: String,
    config: PlantRenderConfig,
    plants: List<List<PlantAppearance>>,
) {
    val numberGenerator = RandomNumberGenerator(Random(System.currentTimeMillis()))
    val dataMap = mutableMapOf<PlantAppearance, Pair<PlantData, PaddedSize>>()
    val size = plants.fold(MIN_SIZE) { rowSize, list ->
        list.fold(rowSize) { columnSize, plant ->
            val data = buildPlant(config, numberGenerator, plant)
            val size = calculateSize(config, data) ?: PaddedSize(MIN_SIZE)
            dataMap[plant] = Pair(data, size)

            columnSize.max(size.getFullSize())
        }
    }

    renderTable(filename, size, plants) { renderAabb, renderer, plant ->
        val (data, paddedSize) = dataMap[plant]!!
        val innerAabb = paddedSize.getInnerAABB(renderAabb)
        val renderState = PlantRenderState(
            state,
            PLANT_CONFIG,
            renderer,
        )

        renderer.getLayer().renderRectangle(renderAabb, BorderOnly(config.line))

        visualizePlant(renderState, data, innerAabb.getPoint(HALF, END))
    }
}