package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
data class MemberRank(
    val name: String,
    val members: Set<CharacterId> = emptySet(),
)
