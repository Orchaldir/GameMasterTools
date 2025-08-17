package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WarParticipantType {
    God,
    Organization,
    Realm,
    Town,
}

@Serializable
sealed class WarParticipant {

    fun getType() = when (this) {
        is ParticipatingGod -> WarParticipantType.God
        is ParticipatingOrganization -> WarParticipantType.Organization
        is ParticipatingRealm -> WarParticipantType.Realm
        is ParticipatingTown -> WarParticipantType.Town
    }
}

@Serializable
@SerialName("God")
data class ParticipatingGod(
    val god: GodId,
) : WarParticipant()

@Serializable
@SerialName("Organization")
data class ParticipatingOrganization(
    val organization: OrganizationId,
) : WarParticipant()

@Serializable
@SerialName("Realm")
data class ParticipatingRealm(
    val realm: RealmId,
) : WarParticipant()

@Serializable
@SerialName("Town")
data class ParticipatingTown(
    val town: TownId,
) : WarParticipant()