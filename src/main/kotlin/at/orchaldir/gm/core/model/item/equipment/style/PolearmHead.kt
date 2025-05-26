package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.Segments
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
