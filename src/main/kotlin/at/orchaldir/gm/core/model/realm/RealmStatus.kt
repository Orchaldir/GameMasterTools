package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class RealmStatusType {
    Abandoned,
    Battle,
    Living,
    Catastrophe,
    War,
    Undefined,
}

@Serializable
sealed class RealmStatus {

    fun getType() = when (this) {
        is Abandoned -> RealmStatusType.Abandoned
        LivingRealm -> RealmStatusType.Living
        is DestroyedByBattle -> RealmStatusType.Battle
        is DestroyedByCatastrophe -> RealmStatusType.Catastrophe
        is DestroyedByWar -> RealmStatusType.War
        is UndefinedEndOfRealm -> RealmStatusType.Undefined
    }

    fun endDate() = when (this) {
        is Abandoned -> date
        LivingRealm -> null
        is DestroyedByBattle -> date
        is DestroyedByCatastrophe -> date
        is DestroyedByWar -> date
        is UndefinedEndOfRealm -> date
    }

    fun isDestroyedByBattle(battle: BattleId) =
        this is DestroyedByBattle && this.battle == battle

    fun isDestroyedByCatastrophe(catastrophe: CatastropheId) =
        this is DestroyedByCatastrophe && this.catastrophe == catastrophe

    fun isDestroyedByWar(war: WarId) = this is DestroyedByWar && this.war == war

}

@Serializable
@SerialName("Abandoned")
data class Abandoned(
    val date: Date,
) : RealmStatus()

@Serializable
@SerialName("Living")
data object LivingRealm : RealmStatus()

@Serializable
@SerialName("Battle")
data class DestroyedByBattle(
    val battle: BattleId,
    val date: Date,
) : RealmStatus()

@Serializable
@SerialName("Catastrophe")
data class DestroyedByCatastrophe(
    val catastrophe: CatastropheId,
    val date: Date,
) : RealmStatus()

@Serializable
@SerialName("War")
data class DestroyedByWar(
    val war: WarId,
    val date: Date,
) : RealmStatus()

@Serializable
@SerialName("UndefinedEnd")
data class UndefinedEndOfRealm(
    val date: Date,
) : RealmStatus()
