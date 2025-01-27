package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.util.History

data class MembershipStorage(
    val data: Map<CharacterId, Map<OrganizationId, History<MembershipStatus>>>,
) {
    fun getMembershipStatus(character: CharacterId, organization: OrganizationId) =
        data[character]?.get(organization)
}
