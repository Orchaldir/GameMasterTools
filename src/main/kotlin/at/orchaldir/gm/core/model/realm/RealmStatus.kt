package at.orchaldir.gm.core.model.realm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RealmStatusType {
    Living,
    Catastrophe,
    War,
    Undefined,
}

@Serializable
sealed class RealmStatus {

    fun getType() = when (this) {
        LivingRealm -> RealmStatusType.Living
        is DestroyedByCatastrophe -> RealmStatusType.Catastrophe
        is DestroyedByWar -> RealmStatusType.War
        UndefinedEndOfRealm -> RealmStatusType.Undefined
    }

}

@Serializable
@SerialName("Living")
data object LivingRealm : RealmStatus()

@Serializable
@SerialName("Catastrophe")
data class DestroyedByCatastrophe(val catastrophe: CatastropheId) : RealmStatus()

@Serializable
@SerialName("War")
data class DestroyedByWar(val war: WarId) : RealmStatus()

@Serializable
@SerialName("UndefinedEnd")
data object UndefinedEndOfRealm : RealmStatus()
