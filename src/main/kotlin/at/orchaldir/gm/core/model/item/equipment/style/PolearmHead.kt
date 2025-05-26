package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_SEGMENT_LENGTH = Factor.fromPercentage(1)
val MAX_SEGMENT_LENGTH = Factor.fromPercentage(120)
val MIN_SEGMENT_DIAMETER = Factor.fromPercentage(10)
val MAX_SEGMENT_DIAMETER = Factor.fromPercentage(200)

enum class PolearmHeadType {
    None,
    Rounded,
    Sharpened,
    Segments,
}

@Serializable
sealed class PolearmHead : MadeFromParts {

    fun getType() = when (this) {
        is NoPolearmHead -> PolearmHeadType.None
        is RoundedPolearmHead -> PolearmHeadType.Rounded
        is SharpenedPolearmHead -> PolearmHeadType.Sharpened
        is PolearmHeadWithSegments -> PolearmHeadType.Segments
    }
}

@Serializable
@SerialName("None")
data object NoPolearmHead : PolearmHead()

@Serializable
@SerialName("Rounded")
data object RoundedPolearmHead : PolearmHead()

@Serializable
@SerialName("Sharpened")
data object SharpenedPolearmHead : PolearmHead()

@Serializable
@SerialName("Segments")
data class PolearmHeadWithSegments(
    val segments: Segments,
) : PolearmHead()
