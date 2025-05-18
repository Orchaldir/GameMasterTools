package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@Serializable
data class TreatyParticipant(
    val realm: RealmId,
    val signature: CharacterId? = null,
) {

    fun <ID : Id<ID>> isCreatedBy(id: ID) = id == realm || id == signature

}