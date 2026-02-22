package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.ReferenceType
import kotlinx.serialization.Serializable

val ALLOWED_WAR_PARTICIPANTS = listOf(
    ReferenceType.Undefined,
    ReferenceType.Culture,
    ReferenceType.God,
    ReferenceType.Organization,
    ReferenceType.Realm,
    ReferenceType.Settlement,
)

@Serializable
data class WarParticipant(
    val reference: Reference,
    val side: History<Int?> = History(0),
)