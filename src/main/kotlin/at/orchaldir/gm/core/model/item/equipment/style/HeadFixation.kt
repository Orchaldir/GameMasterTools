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

val MIN_LANGETS_LENGTH = fromPercentage(10)
val DEFAULT_LANGETS_LENGTH = fromPercentage(20)
val MAX_LANGETS_LENGTH = fromPercentage(40)

enum class HeadFixationType {
    None,
    Bound,
    Langets,
    Socketed,
}

@Serializable
sealed class HeadFixation : MadeFromParts {

    fun getType() = when (this) {
        NoHeadFixation -> HeadFixationType.None
        is BoundHeadHead -> HeadFixationType.Bound
        is Langets -> HeadFixationType.Langets
        is SocketedHeadHead -> HeadFixationType.Socketed
    }

    override fun parts() = when (this) {
        NoHeadFixation -> emptyList()
        is BoundHeadHead -> listOf(part)
        is Langets -> listOf(part)
        is SocketedHeadHead -> listOf(part)
    }
}

@Serializable
@SerialName("None")
data object NoHeadFixation : HeadFixation()

@Serializable
@SerialName("Bound")
data class BoundHeadHead(
    val length: Factor = DEFAULT_FIXATION_LENGTH,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HeadFixation()

@Serializable
@SerialName("Langets")
data class Langets(
    val length: Factor = DEFAULT_LANGETS_LENGTH,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HeadFixation()

@Serializable
@SerialName("Socketed")
data class SocketedHeadHead(
    val length: Factor = DEFAULT_FIXATION_LENGTH,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HeadFixation()


