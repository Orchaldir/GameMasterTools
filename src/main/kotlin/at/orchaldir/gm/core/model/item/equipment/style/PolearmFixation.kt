package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_FIXATION_LENGTH = fromPercentage(1)
val DEFAULT_FIXATION_LENGTH = fromPercentage(10)
val MAX_FIXATION_LENGTH = fromPercentage(20)

enum class PolearmFixationType {
    None,
    Bound,
    Langets,
    Socketed,
}

@Serializable
sealed class PolearmFixation : MadeFromParts {

    fun getType() = when (this) {
        NoPolearmFixation -> PolearmFixationType.None
        is BoundPolearmHead -> PolearmFixationType.Bound
        is Langets -> PolearmFixationType.Langets
        is SocketedPolearmHead -> PolearmFixationType.Socketed
    }

    override fun parts() = when (this) {
        NoPolearmFixation -> emptyList()
        is BoundPolearmHead -> listOf(part)
        is Langets -> listOf(part)
        is SocketedPolearmHead -> listOf(part)
    }
}

@Serializable
@SerialName("None")
data object NoPolearmFixation : PolearmFixation()

@Serializable
@SerialName("Bound")
data class BoundPolearmHead(
    val length: Factor = DEFAULT_FIXATION_LENGTH,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : PolearmFixation()

@Serializable
@SerialName("Langets")
data class Langets(
    val length: Factor = DEFAULT_FIXATION_LENGTH,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : PolearmFixation()

@Serializable
@SerialName("Socketed")
data class SocketedPolearmHead(
    val length: Factor = DEFAULT_FIXATION_LENGTH,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : PolearmFixation()


