package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ShotsType {
    Thrown,
    SingleShot,
    Undefined,
}

@Serializable
sealed class Shots {

    fun getType() = when (this) {
        is Thrown -> ShotsType.Thrown
        is SingleShot -> ShotsType.SingleShot
        is UndefinedShots -> ShotsType.Undefined
    }

    fun contains(type: AmmunitionTypeId) = when (this) {
        is SingleShot -> ammunition == type
        is Thrown -> false
        UndefinedShots -> false
    }
}

@Serializable
@SerialName("Thrown")
data class Thrown(
    val roundsOfReload: Int = 1,
) : Shots()

@Serializable
@SerialName("SingleShot")
data class SingleShot(
    val ammunition: AmmunitionTypeId,
    val roundsOfReload: Int = 1,
) : Shots()

@Serializable
@SerialName("Undefined")
data object UndefinedShots : Shots()
