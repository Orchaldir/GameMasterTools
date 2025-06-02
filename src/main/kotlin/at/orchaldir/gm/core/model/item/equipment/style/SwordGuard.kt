package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
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
    val grip: SwordGrip = SwordGrip(),
    val pommel: Pommel = Pommel(),
) : SwordGuard()
