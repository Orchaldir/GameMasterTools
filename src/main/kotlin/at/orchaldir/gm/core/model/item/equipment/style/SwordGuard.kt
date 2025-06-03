package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_GUARD_WIDTH = Factor.fromPercentage(100)
val DEFAULT_GUARD_WIDTH = Factor.fromPercentage(300)
val MAX_GUARD_WIDTH = Factor.fromPercentage(500)

val MIN_GUARD_HEIGHT = Factor.fromPercentage(1)
val DEFAULT_GUARD_HEIGHT = Factor.fromPercentage(5)
val MAX_GUARD_HEIGHT = Factor.fromPercentage(10)

enum class SwordGuardType {
    None,
    Simple,
}

@Serializable
sealed class SwordGuard : MadeFromParts {

    fun getType() = when (this) {
        NoSwordGuard -> SwordGuardType.None
        is SimpleSwordGuard -> SwordGuardType.Simple
    }

    override fun parts() = when (this) {
        NoSwordGuard -> emptyList()
        is SimpleSwordGuard -> listOf(part)
    }
}

@Serializable
@SerialName("None")
data object NoSwordGuard : SwordGuard()

@Serializable
@SerialName("Simple")
data class SimpleSwordGuard(
    /**
     * Relative to the grip's width
     */
    val width: Factor = DEFAULT_GUARD_WIDTH,
    /**
     * Relative to the character's height
     */
    val height: Factor = DEFAULT_GUARD_HEIGHT,
    val part: FillLookupItemPart = FillLookupItemPart(),
) : SwordGuard()
