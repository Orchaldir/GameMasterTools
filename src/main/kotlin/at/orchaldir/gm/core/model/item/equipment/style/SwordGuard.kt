package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    override fun parts() = emptyList<ItemPart>()
}

@Serializable
@SerialName("None")
data object NoSwordGuard : SwordGuard()

@Serializable
@SerialName("Simple")
data class SimpleSwordGuard(
    val width: Factor = Factor.fromPercentage(10),
    val height: Factor = Factor.fromPercentage(20),
    val part: FillLookupItemPart = FillLookupItemPart(),
) : SwordGuard()
