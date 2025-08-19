package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.ReferenceType
import kotlinx.serialization.Serializable

val ALLOWED_WAR_PARTICIPANTS = ReferenceType.entries - ReferenceType.Business - ReferenceType.Character

@Serializable
data class WarParticipant(
    val reference: Reference,
    val side: History<Int?> = History(0),
)