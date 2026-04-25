package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Orientation
import kotlinx.serialization.Serializable

@Serializable
data class Stem(
    val segments: Int,
    /**
     * The probability of the stem splitting after each segment.
     */
    val segmentSplit: Factor,
    /**
     * The probability of the stem splitting after the 1.segment.
     */
    val baseSplit: Factor,
    /**
     * The angle that all stems of a split rotate away from the original orientation.
     */
    val splitAngle: Orientation,
    /**
     * The variation of the **splitAngle** for each split.
     */
    val splitAngleVariation: Orientation,
)
