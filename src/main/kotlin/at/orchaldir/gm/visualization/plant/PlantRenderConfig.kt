package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.ecology.plant.Tree
import at.orchaldir.gm.core.model.ecology.plant.appearance.TreeSilhouetteShape
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.PI_FACTOR
import at.orchaldir.gm.utils.math.THIRD
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.TWO_THIRD
import at.orchaldir.gm.utils.math.ZERO
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

        return when (shape) {
            TreeSilhouetteShape.Conical -> resolveTreeSilhouetteShape(min, inverted)
            TreeSilhouetteShape.Spherical -> resolveTreeSilhouetteShape(
                min,
                (inverted * PI_FACTOR).sin(),
            )
            TreeSilhouetteShape.Hemispherical -> resolveTreeSilhouetteShape(
                min,
                if (position < HALF) {
                    FULL
                } else {
                    val position = (position - HALF) / HALF
                    val inverted = FULL - position
                    (inverted * PI_FACTOR * 0.5f).sin()
                },
            )
            TreeSilhouetteShape.Cylindrical -> FULL
            TreeSilhouetteShape.Flame -> resolveTreeSilhouetteShape(
                min,
                if (position < THIRD) {
                    position / THIRD
                } else {
                    val position = (position - THIRD) / TWO_THIRD
                    val inverted = FULL - position
                    (inverted * PI_FACTOR * 0.5f).sin()
                }
            )
        }
    }

    private fun resolveTreeSilhouetteShape(min: Factor, factor: Factor) = min + (FULL - min) * factor

}