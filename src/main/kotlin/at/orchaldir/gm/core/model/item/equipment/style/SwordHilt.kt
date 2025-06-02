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
        is HiltWithoutGuard -> pommel.parts()
        is SimpleHilt -> pommel.parts()
    }
}

@Serializable
@SerialName("WithoutGuard")
data class HiltWithoutGuard(
    val pommel: Pommel = Pommel(),
) : SwordHilt()

@Serializable
@SerialName("Simple")
data class SimpleHilt(
    val pommel: Pommel = Pommel(),
) : SwordHilt()
