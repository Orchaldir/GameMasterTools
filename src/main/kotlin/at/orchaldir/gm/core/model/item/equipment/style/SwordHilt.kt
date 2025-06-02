package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SwordHiltType {
    Simple,
    WithoutGuard,
}

@Serializable
sealed class SwordHilt : MadeFromParts {

    fun getType() = when (this) {
        is HiltWithoutGuard -> SwordHiltType.WithoutGuard
        is SimpleHilt -> SwordHiltType.Simple
    }

    override fun parts() = when (this) {
        is HiltWithoutGuard -> grip.parts() + pommel.parts()
        is SimpleHilt -> grip.parts() + pommel.parts()
    }
}

@Serializable
@SerialName("WithoutGuard")
data class HiltWithoutGuard(
    val grip: SwordGrip = SwordGrip(),
    val pommel: Pommel = Pommel(),
) : SwordHilt()

@Serializable
@SerialName("Simple")
data class SimpleHilt(
    val grip: SwordGrip = SwordGrip(),
    val pommel: Pommel = Pommel(),
) : SwordHilt()
