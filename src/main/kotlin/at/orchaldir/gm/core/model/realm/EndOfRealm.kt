package at.orchaldir.gm.core.model.realm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EndOfRealmType {
    Catastrophe,
    War,
    Undefined,
}

@Serializable
sealed class EndOfRealm {

    fun getType() = when (this) {
        is DestroyedByCatastrophe -> EndOfRealmType.Catastrophe
        is DestroyedByWar -> EndOfRealmType.War
        UndefinedEndOfRealm -> EndOfRealmType.Undefined
    }

}

@Serializable
@SerialName("Catastrophe")
data class DestroyedByCatastrophe(val catastrophe: CatastropheId) : EndOfRealm()

@Serializable
@SerialName("War")
data class DestroyedByWar(val war: WarId) : EndOfRealm()

@Serializable
@SerialName("Undefined")
data object UndefinedEndOfRealm : EndOfRealm()
