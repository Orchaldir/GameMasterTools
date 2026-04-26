package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val angle: Variance<Orientation>,
) : StemSplitting()

@Serializable
@SerialName("Base")
data class BaseSplitting(
    val probability: Factor,
    /**
     * The angle that all stems of a split rotate away from the original orientation.
     */
    val angle: Variance<Orientation>,
) : StemSplitting()