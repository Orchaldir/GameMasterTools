package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EventReferenceType {
    Battle,
    Catastrophe,
    Treaty,
    War,
    Undefined,
}

@Serializable
sealed class EventReference {

    fun getType() = when (this) {
        is BattleReference -> EventReferenceType.Battle
        is CatastropheReference -> EventReferenceType.Catastrophe
        is TreatyReference -> EventReferenceType.Treaty
        is WarReference -> EventReferenceType.War
        is UndefinedEventReference -> EventReferenceType.Undefined
    }

    fun <ID : Id<ID>> isId(id: ID) = when (this) {
        is BattleReference -> battle == id
        is CatastropheReference -> catastrophe == id
        is TreatyReference -> treaty == id
        is WarReference -> war == id
        is UndefinedEventReference -> false
    }

}


@Serializable
@SerialName("Undefined")
data object UndefinedEventReference : EventReference()

@Serializable
@SerialName("Battle")
data class BattleReference(val battle: BattleId) : EventReference()

@Serializable
@SerialName("Catastrophe")
data class CatastropheReference(val catastrophe: CatastropheId) : EventReference()

@Serializable
@SerialName("Treaty")
data class TreatyReference(val treaty: TreatyId) : EventReference()

@Serializable
@SerialName("War")
data class WarReference(val war: WarId) : EventReference()