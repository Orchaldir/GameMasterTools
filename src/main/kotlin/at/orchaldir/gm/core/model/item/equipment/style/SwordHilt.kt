package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SwordHiltType {
    Simple,
}

@Serializable
sealed class SwordHilt : MadeFromParts {

    fun getType() = when (this) {
        is SimpleSwordHilt -> SwordHiltType.Simple
    }

    override fun parts() = when (this) {
        is SimpleSwordHilt -> guard.parts() + grip.parts() + pommel.parts()
    }
}

@Serializable
@SerialName("Simple")
data class SimpleSwordHilt(
    val guard: SwordGuard = SimpleSwordGuard(),
    val grip: Grip = SimpleGrip(),
    val pommel: Pommel = PommelWithOrnament(),
) : SwordHilt()
