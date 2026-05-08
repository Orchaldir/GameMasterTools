package at.orchaldir.gm.visualization.plant

import at.orchaldir.gm.core.model.ecology.plant.appearance.BranchLength
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.PI_FACTOR
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions

data class PlantRenderConfig(
    val line: LineOptions,
    val padding: Factor,
    val minBranchLength: Factor,
) {

    fun getFillAndBorder(color: Color) = FillAndBorder(color.toRender(), line)

    fun resolveBranchLength(length: BranchLength, position: Factor): Factor {
        val inverted = FULL - position

        return when (length) {
            BranchLength.Conical -> simpleBranchLength(inverted)
            BranchLength.Spherical -> simpleBranchLength((inverted * PI_FACTOR).sin())
            BranchLength.Hemispherical -> simpleBranchLength((inverted * PI_FACTOR * 0.5f).sin())
            BranchLength.Cylindrical -> FULL
            BranchLength.Flame -> simpleBranchLength(
                if (position.toNumber() < 0.3f) {
                    position / 0.3f
                } else {
                    (FULL - position) / 0.7f
                }
            )
        }
    }

    private fun simpleBranchLength(factor: Factor) = minBranchLength + (FULL - minBranchLength) * factor

}