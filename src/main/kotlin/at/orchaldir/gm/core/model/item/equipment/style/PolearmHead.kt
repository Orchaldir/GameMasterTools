package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_SEGMENT_LENGTH = fromPercentage(1)
val MAX_SEGMENT_LENGTH = fromPercentage(120)
val MIN_SEGMENT_DIAMETER = fromPercentage(10)
val MAX_SEGMENT_DIAMETER = fromPercentage(200)

enum class PolearmHeadType {
    None,
    Rounded,
    Sharpened,
    Segments,
    Spear,
}

@Serializable
sealed class PolearmHead : MadeFromParts {

    fun getType() = when (this) {
        is NoPolearmHead -> PolearmHeadType.None
        is RoundedPolearmHead -> PolearmHeadType.Rounded
        is SharpenedPolearmHead -> PolearmHeadType.Sharpened
        is PolearmHeadWithSegments -> PolearmHeadType.Segments
        is SpearHead -> PolearmHeadType.Spear
    }

    override fun parts() = when (this) {
        NoPolearmHead -> emptyList()
        RoundedPolearmHead -> emptyList()
        SharpenedPolearmHead -> emptyList()
        is PolearmHeadWithSegments -> segments.parts()
        is SpearHead -> fixation.parts() + head
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

@Serializable
@SerialName("Spear")
data class SpearHead(
    val shape: SpearShape = SpearShape.Leaf,
    val length: Factor = fromPercentage(10),
    val width: Factor = length / 2,
    val head: ColorSchemeItemPart = ColorSchemeItemPart(),
    val fixation: PolearmFixation = NoPolearmFixation,
) : PolearmHead()
