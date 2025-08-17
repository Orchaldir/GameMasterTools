package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.name.Name
import kotlinx.serialization.Serializable

@Serializable
data class WarSide(
    val participants: List<WarParticipant>,
    val name: Name? = null,
)