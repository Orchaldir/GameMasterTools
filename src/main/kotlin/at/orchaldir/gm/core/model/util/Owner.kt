package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OwnerType {
    None,
    Undefined,
    Business,
    Character,
    Organization,
    Realm,
    Town,
}

@Serializable
sealed class Owner {

    fun getType() = when (this) {
        NoOwner -> OwnerType.None
        is OwnedByBusiness -> OwnerType.Business
        is OwnedByCharacter -> OwnerType.Character
        is OwnedByOrganization -> OwnerType.Organization
        is OwnedByRealm -> OwnerType.Realm
        is OwnedByTown -> OwnerType.Town
        UndefinedOwner -> OwnerType.Undefined
    }

    fun canDelete() = when (this) {
        is OwnedByBusiness -> false
        is OwnedByCharacter -> false
        is OwnedByOrganization -> false
        is OwnedByRealm -> false
        is OwnedByTown -> false
        NoOwner, UndefinedOwner -> true
    }

    fun <ID : Id<ID>> isOwnedBy(id: ID) = when (this) {
        is OwnedByBusiness -> business == id
        is OwnedByCharacter -> character == id
        is OwnedByOrganization -> organization == id
        is OwnedByRealm -> realm == id
        is OwnedByTown -> town == id
        NoOwner, UndefinedOwner -> false
    }

}


@Serializable
@SerialName("None")
data object NoOwner : Owner()

@Serializable
@SerialName("Undefined")
data object UndefinedOwner : Owner()

@Serializable
@SerialName("Business")
data class OwnedByBusiness(val business: BusinessId) : Owner()

@Serializable
@SerialName("Character")
data class OwnedByCharacter(val character: CharacterId) : Owner()

@Serializable
@SerialName("Organization")
data class OwnedByOrganization(val organization: OrganizationId) : Owner()

@Serializable
@SerialName("Realm")
data class OwnedByRealm(val realm: RealmId) : Owner()

@Serializable
@SerialName("Town")
data class OwnedByTown(val town: TownId) : Owner()


