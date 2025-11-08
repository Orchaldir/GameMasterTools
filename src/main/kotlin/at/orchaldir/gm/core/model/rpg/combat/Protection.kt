package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ProtectionType {
    DamageResistance,
    Undefined,
}

@Serializable
sealed class Protection {

    fun getType() = when (this) {
        is DamageResistance -> ProtectionType.DamageResistance
        is UndefinedProtection -> ProtectionType.Undefined
    }
}

@Serializable
@SerialName("DamageResistance")
data class DamageResistance(
    val amount: Int,
) : Protection()

@Serializable
@SerialName("Undefined")
data object UndefinedProtection : Protection()
