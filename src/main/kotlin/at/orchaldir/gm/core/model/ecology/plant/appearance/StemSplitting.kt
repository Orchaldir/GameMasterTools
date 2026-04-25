package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Orientation
import kotlinx.serialization.Serializable

@Serializable
data class StemSplitting(
    /**
     * The probability of the stem splitting after each segment.
     */
    val segmentProbability: Factor,
    /**
     * The probability of the stem splitting after the 1.segment.
     */
    val baseProbability: Factor,
    /**
     * The angle that all stems of a split rotate away from the original orientation.
     */
    val angle: Orientation,
    /**
     * The variation of the **angle** for each split.
     */
    val angleVariation: Orientation,
)
