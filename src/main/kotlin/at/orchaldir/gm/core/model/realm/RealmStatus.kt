package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
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
        is UndefinedEndOfRealm -> RealmStatusType.Undefined
    }

    fun endDate() = when (this) {
        LivingRealm -> null
        is DestroyedByCatastrophe -> date
        is DestroyedByWar -> date
        is UndefinedEndOfRealm -> date
    }

    fun isDestroyedByCatastrophe(catastrophe: CatastropheId) =
        this is DestroyedByCatastrophe && this.catastrophe == catastrophe
    fun isDestroyedByWar(war: WarId) = this is DestroyedByWar && this.war == war

}

@Serializable
@SerialName("Living")
data object LivingRealm : RealmStatus()

@Serializable
@SerialName("Catastrophe")
data class DestroyedByCatastrophe(
    val catastrophe: CatastropheId,
    val date: Date? = null,
) : RealmStatus()

@Serializable
@SerialName("War")
data class DestroyedByWar(
    val war: WarId,
    val date: Date? = null,
) : RealmStatus()

@Serializable
@SerialName("UndefinedEnd")
data class UndefinedEndOfRealm(
    val date: Date? = null,
) : RealmStatus()
