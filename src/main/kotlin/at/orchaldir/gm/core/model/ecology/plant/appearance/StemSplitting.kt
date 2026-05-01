package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.HALF_CIRCLE
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import at.orchaldir.gm.utils.math.validateFactor
import at.orchaldir.gm.utils.toInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_SPLITTING_PROBABILITY = Factor.fromPercentage(1)
val MAX_SPLITTING_PROBABILITY = DOUBLE
val MIN_SPLITTING_ANGLE = Orientation.fromDegrees(5)
val MAX_SPLITTING_ANGLE = HALF_CIRCLE
val MIN_SPLITTING_OFFSET = ZERO_ORIENTATION
val MAX_SPLITTING_OFFSET = QUARTER_CIRCLE

enum class StemSplittingType {
    None,
    Segment,
    Base,
}

@Serializable
sealed class StemSplitting {

    fun getType() = when (this) {
        NoStemSplitting -> StemSplittingType.None
        is SegmentSplitting -> StemSplittingType.Segment
        is BaseSplitting -> StemSplittingType.Base
    }

    fun calculateSplits(
        numberGenerator: NumberGenerator,
        orientation: Orientation,
        segmentIndex: Int,
    ): List<Orientation> = when (this) {
        NoStemSplitting -> noSplits(orientation)
        is BaseSplitting -> if (segmentIndex == 0) {
            calculateSplits(numberGenerator, orientation, probability, angle)
        } else {
            noSplits(orientation)
        }
        is SegmentSplitting -> calculateSplits(numberGenerator, orientation, probability, angle)
    }

    private fun calculateSplits(
        numberGenerator: NumberGenerator,
        orientation: Orientation,
        probability: Factor,
        angle: Variance<Orientation>,
    ): List<Orientation> {
        if (probability <= ZERO) {
            return noSplits(orientation)
        }

        val defaultNumber = probability.toNumber().toInt()
        val remainingProbability = Factor.fromNumber(probability.toNumber() - defaultNumber)

        return calculateSplits(
            numberGenerator,
            orientation,
            angle,
            1 + defaultNumber + numberGenerator.isTriggered(remainingProbability).toInt()
        )
    }

    private fun calculateSplits(
        numberGenerator: NumberGenerator,
        orientation: Orientation,
        angle: Variance<Orientation>,
        count: Int,
    ) = when (count) {
        1 -> noSplits(orientation)
        2 -> listOf(
            orientation + angle.generate(numberGenerator),
            orientation - angle.generate(numberGenerator),
        )
        else -> error("$count splits is not supported by StemSplitting!")
    }

    private fun noSplits(orientation: Orientation) = listOf(orientation)

    fun validate(label: String) = when (this) {
        NoStemSplitting -> doNothing()
        is BaseSplitting -> {
            validateProbability(label, probability)
            validateAngle(label, angle)
        }
        is SegmentSplitting -> {
            validateProbability(label, probability)
            validateAngle(label, angle)
        }
    }
}

private fun validateProbability(label: String, probability: Factor) {
    validateFactor(
        probability,
        "$label's splitting probability",
        MIN_SPLITTING_PROBABILITY,
        MAX_SPLITTING_PROBABILITY,
    )
}

private fun validateAngle(label: String, angle: Variance<Orientation>) {
    angle.validate(
        "$label's splitting angle",
        MIN_SPLITTING_ANGLE,
        MAX_SPLITTING_ANGLE,
        MIN_SPLITTING_OFFSET,
        MAX_SPLITTING_OFFSET,
    )
}

@Serializable
@SerialName("None")
data object NoStemSplitting : StemSplitting()

@Serializable
@SerialName("Segment")
data class SegmentSplitting(
    val probability: Factor,
    /**
     * The angle that all stems of a split rotate away from the original orientation.
     */
    val angle: Variance<Orientation> = Variance(MIN_SPLITTING_ANGLE),
) : StemSplitting()

@Serializable
@SerialName("Base")
data class BaseSplitting(
    val probability: Factor,
    /**
     * The angle that all stems of a split rotate away from the original orientation.
     */
    val angle: Variance<Orientation> = Variance(MIN_SPLITTING_ANGLE),
) : StemSplitting()