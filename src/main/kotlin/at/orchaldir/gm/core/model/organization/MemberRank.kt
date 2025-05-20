package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.util.name.Name
import kotlinx.serialization.Serializable

@Serializable
data class MemberRank(
    val name: Name = Name.init("Member"),
)
