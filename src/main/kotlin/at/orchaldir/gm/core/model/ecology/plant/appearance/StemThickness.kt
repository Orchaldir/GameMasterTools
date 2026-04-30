package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StemThicknessType {
    Constant,
    Linear,
}

@Serializable
sealed class StemThickness {

    fun getType() = when (this) {
        is ConstantStemThickness -> StemThicknessType.Constant
        is LinearStemThickness -> StemThicknessType.Linear
    }

    fun calculate(length: Distance, position: Factor) = when (this) {
        is ConstantStemThickness -> length * relativeToLength
        is LinearStemThickness -> length * start.interpolate(end, position)
    }

    fun hasRoundedEnd() = when (this) {
        is ConstantStemThickness -> hasRoundedEnd
        is LinearStemThickness -> hasRoundedEnd
    }
}

@Serializable
@SerialName("Constant")
data class ConstantStemThickness(
    val relativeToLength: Factor,
    val hasRoundedEnd: Boolean = false,
) : StemThickness()

@Serializable
@SerialName("Linear")
data class LinearStemThickness(
    /*
     * The thickness at the start of the stem relative to the length of the stem.
     */
    val start: Factor,
    /*
     * The thickness at the end of the stem relative to the length of the stem.
     */
    val end: Factor = ZERO,
    val hasRoundedEnd: Boolean = false,
) : StemThickness()