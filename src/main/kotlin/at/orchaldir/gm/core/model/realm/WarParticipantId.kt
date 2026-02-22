package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WarParticipantIdType {
    God,
    Organization,
    Realm,
    Settlement,
}

@Serializable
sealed class WarParticipantId {

    fun getType() = when (this) {
        is ParticipatingGod -> WarParticipantIdType.God
        is ParticipatingOrganization -> WarParticipantIdType.Organization
        is ParticipatingRealm -> WarParticipantIdType.Realm
        is ParticipatingSettlement -> WarParticipantIdType.Settlement
    }
}

@Serializable
@SerialName("God")
data class ParticipatingGod(
    val god: GodId,
) : WarParticipantId()

@Serializable
@SerialName("Organization")
data class ParticipatingOrganization(
    val organization: OrganizationId,
) : WarParticipantId()

@Serializable
@SerialName("Realm")
data class ParticipatingRealm(
    val realm: RealmId,
) : WarParticipantId()

@Serializable
@SerialName("Settlement")
data class ParticipatingSettlement(
    val settlement: SettlementId,
) : WarParticipantId()