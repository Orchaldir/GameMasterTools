package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.utils.math.Size2dCalculator
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import at.orchaldir.gm.visualization.plant.builder.*

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
): PaddedSize {
    val calculator = Size2dCalculator()

    processStem(calculator, tree.trunk)

    tree.silhouette.forEach {
        processSilhouette(calculator, it)
    }

    val size = calculator.calculate()
    val padding = size.maxSize() * config.padding

    return PaddedSize(size, padding)
}

private fun processStem(
    calculator: Size2dCalculator,
    stem: StemData,
) {
    val (left, right) = stem.start.createLeftAndRightPoint(stem.segment.orientation, stem.thickness)

    calculator.process(left)
    calculator.process(right)

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
    segment.branches.forEach {
        processStem(calculator, it)
    }
}

private fun processSilhouette(
    calculator: Size2dCalculator,
    silhouette: SilhouetteData,
) {
    silhouette.polygon.corners.forEach {
        calculator.process(it)
    }
}