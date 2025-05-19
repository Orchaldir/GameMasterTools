package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
data class BattleParticipant(
    val realm: RealmId,
    val leader: CharacterId? = null,
)