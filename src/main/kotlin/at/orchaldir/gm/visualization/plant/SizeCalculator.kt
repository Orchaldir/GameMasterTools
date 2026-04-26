package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2dCalculator
import at.orchaldir.gm.visualization.plant.builder.PlantData
import at.orchaldir.gm.visualization.plant.builder.SegmentData
import at.orchaldir.gm.visualization.plant.builder.StemData
import at.orchaldir.gm.visualization.plant.builder.TreeData
import at.orchaldir.gm.visualization.plant.builder.UndefinedPlantData

fun calculateSize(
    config: PlantRenderConfig,
    plant: PlantData,
) = when (plant) {
    is TreeData -> calculateTreeSize(config, plant)
    UndefinedPlantData -> null
}

private fun calculateTreeSize(
    config: PlantRenderConfig,
    tree: TreeData,
): Size2d {
    val calculator = Size2dCalculator()

    processStem(calculator, tree.trunk)

    return calculator.calculate() * (FULL + config.padding * 2.0f)
}

private fun processStem(
    calculator: Size2dCalculator,
    stem: StemData,
) {
    calculator.process(stem.start)

    processSegment(calculator, stem.segment)
}

private fun processSegment(
    calculator: Size2dCalculator,
    segment: SegmentData,
) {
    calculator.process(segment.end)

    segment.next.forEach {
        processSegment(calculator, it)
    }
}
