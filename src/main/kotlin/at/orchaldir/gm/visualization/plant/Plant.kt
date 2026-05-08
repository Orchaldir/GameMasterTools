package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.PlantAppearance
import at.orchaldir.gm.prototypes.visualization.plant.PLANT_CONFIG
import at.orchaldir.gm.utils.RandomNumberGenerator
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.plant.builder.buildPlant
import at.orchaldir.gm.visualization.plant.visualization.visualizePlant
import kotlin.random.Random

fun visualizePlant(
    config: PlantRenderConfig,
    state: State,
    plant: PlantAppearance,
): Svg {
    val numberGenerator = RandomNumberGenerator(Random(System.currentTimeMillis()))
    val data = buildPlant(config, numberGenerator, plant)
    val size = calculateSize(config, data) ?: PaddedSize(Size2d.fromMeters(1.0f))
    val innerAabb = size.getInnerAABB()
    val renderer = SvgBuilder(size.getFullSize())
    val renderState = PlantRenderState(
        state,
        PLANT_CONFIG,
        renderer,
    )

    renderer.getLayer().renderRectangle(innerAabb, BorderOnly(config.line))

    visualizePlant(renderState, data, innerAabb.getPoint(HALF, END))

    return renderer.finish()
}