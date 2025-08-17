package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class WarParticipant(
    val id: WarParticipantId,
    val side: History<Int?>,
)