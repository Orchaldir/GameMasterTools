package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouetteShape
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions

data class PlantRenderConfig(
    val line: LineOptions,
    val padding: Factor,
    val minBranchLength: Factor,
    val silhouetteStepsPerSegment: Int,
    val minSilhouetteNarrow: Factor,
    val minSilhouetteWide: Factor,
) {
    fun calculateSilhouetteSteps(tree: Tree) = tree.trunk.stem.segments * silhouetteStepsPerSegment

    fun getFillAndBorder(color: Color) = FillAndBorder(color.toRender(), line)

    fun resolveBranchLength(length: TreeSilhouetteShape, position: Factor) =
        resolveTreeSilhouetteShape(length, minBranchLength, position)

    fun resolveTreeSilhouetteShape(shape: TreeSilhouetteShape, position: Factor) =
        resolveTreeSilhouetteShape(shape, getMin(shape), position)

    private fun getMin(shape: TreeSilhouetteShape) = when (shape) {
        TreeSilhouetteShape.Conical, TreeSilhouetteShape.Flame -> minSilhouetteNarrow
        TreeSilhouetteShape.Spherical, TreeSilhouetteShape.Hemispherical -> minSilhouetteWide
        TreeSilhouetteShape.Cylindrical -> ZERO
    }

    private fun resolveTreeSilhouetteShape(shape: TreeSilhouetteShape, min: Factor, position: Factor): Factor {
        val inverted = FULL - position

        val mapped = when (shape) {
            TreeSilhouetteShape.Conical -> inverted
            TreeSilhouetteShape.Spherical -> (inverted * PI_FACTOR).sin()
            TreeSilhouetteShape.Hemispherical -> if (position < HALF) {
                FULL
            } else {
                calculateCurve(position, HALF)
            }

            TreeSilhouetteShape.Cylindrical -> return FULL
            TreeSilhouetteShape.Flame -> if (position < THIRD) {
                calculateCurve(position, ZERO, THIRD, PI_2_FACTOR)
            } else {
                calculateCurve(position, THIRD)
            }
        }

        return resolveTreeSilhouetteShape(min, mapped)
    }

    private fun calculateCurve(
        position: Factor,
        min: Factor,
        max: Factor = FULL,
        offset: Factor = ZERO,
    ): Factor {
        val scaled = (position - min) / (max - min)
        val inverted = FULL - scaled

        return (inverted * PI_FACTOR * 0.5f + offset).sin()
    }

    private fun resolveTreeSilhouetteShape(min: Factor, factor: Factor) = min + (FULL - min) * factor

}