package at.orchaldir.gm.core.model.organization

import kotlinx.serialization.Serializable

@Serializable
data class MemberRank(
    val name: String = "Member",
)
