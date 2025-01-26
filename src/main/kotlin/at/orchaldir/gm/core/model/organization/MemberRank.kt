package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
data class MemberRank(
    val name: String = "Member",
    val maxNumber: Int? = null,
    val members: List<CharacterId> = emptyList(),
)
