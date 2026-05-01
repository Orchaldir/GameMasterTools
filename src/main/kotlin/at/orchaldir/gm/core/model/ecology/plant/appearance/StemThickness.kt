package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.checkInt
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.validateFactor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_RELATIVE_TO_LENGTH = Factor.fromPermille(1)
val DEFAULT_RELATIVE_TO_LENGTH = Factor.fromPercentage(5)
val MAX_RELATIVE_TO_LENGTH = FULL

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

    fun validate(label: String) = when (this) {
        is ConstantStemThickness -> validateFactor(
            relativeToLength,
            "$label's thickness",
            MIN_RELATIVE_TO_LENGTH,
            MAX_RELATIVE_TO_LENGTH,
        )
        is LinearStemThickness -> doNothing()
    }
}

@Serializable
@SerialName("Constant")
data class ConstantStemThickness(
    val relativeToLength: Factor = DEFAULT_RELATIVE_TO_LENGTH,
    val hasRoundedEnd: Boolean = false,
) : StemThickness()

@Serializable
@SerialName("Linear")
data class LinearStemThickness(
    /*
     * The thickness at the start of the stem relative to the length of the stem.
     */
    val start: Factor = DEFAULT_RELATIVE_TO_LENGTH,
    /*
     * The thickness at the end of the stem relative to the length of the stem.
     */
    val end: Factor = ZERO,
    val hasRoundedEnd: Boolean = false,
) : StemThickness()