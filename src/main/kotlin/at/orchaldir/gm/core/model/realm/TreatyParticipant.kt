package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
data class TreatyParticipant(
    val realm: RealmId,
    val signature: CharacterId? = null,
)