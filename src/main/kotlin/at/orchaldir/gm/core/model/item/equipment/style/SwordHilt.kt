package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SwordHiltType {
    Simple,
}

@Serializable
sealed class SwordHilt : MadeFromParts {

    fun getType() = when (this) {
        is SimpleHilt -> SwordHiltType.Simple
    }

    override fun parts() = when (this) {
        is SimpleHilt -> guard.parts() + grip.parts() + pommel.parts()
    }
}

@Serializable
@SerialName("Simple")
data class SimpleHilt(
    val guard: SwordGuard = SimpleSwordGuard(),
    val grip: SwordGrip = SwordGrip(),
    val pommel: Pommel = Pommel(),
) : SwordHilt()
