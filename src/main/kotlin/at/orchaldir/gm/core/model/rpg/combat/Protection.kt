package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ProtectionType {
    DamageResistance,
    DamageResistances,
    Undefined,
}

@Serializable
sealed class Protection {

    fun getType() = when (this) {
        is DamageResistance -> ProtectionType.DamageResistance
        is DamageResistances -> ProtectionType.DamageResistances
        is UndefinedProtection -> ProtectionType.Undefined
    }
}

@Serializable
@SerialName("DamageResistance")
data class DamageResistance(
    val amount: Int = 1,
) : Protection()

@Serializable
@SerialName("DamageResistances")
data class DamageResistances(
    val amount: Int = 1,
    val damageTypes: Map<DamageTypeId, Int> = emptyMap(),
) : Protection()

@Serializable
@SerialName("Undefined")
data object UndefinedProtection : Protection()
