package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ReferenceType {
    None,
    Undefined,
    Business,
    Character,
    Culture,
    God,
    Organization,
    Realm,
    Town,
}

@Serializable
sealed class Reference {

    fun getType() = when (this) {
        is NoReference -> ReferenceType.Undefined
        is UndefinedReference -> ReferenceType.Undefined
        is BusinessReference -> ReferenceType.Business
        is CharacterReference -> ReferenceType.Character
        is CultureReference -> ReferenceType.Culture
        is GodReference -> ReferenceType.God
        is OrganizationReference -> ReferenceType.Organization
        is RealmReference -> ReferenceType.Realm
        is TownReference -> ReferenceType.Town
    }

    fun <ID : Id<ID>> isId(id: ID) = when (this) {
        NoReference, UndefinedReference -> false
        is BusinessReference -> business == id
        is CharacterReference -> character == id
        is CultureReference -> culture == id
        is GodReference -> god == id
        is OrganizationReference -> organization == id
        is RealmReference -> realm == id
        is TownReference -> town == id
    }

}

@Serializable
@SerialName("None")
data object NoReference : Reference()

@Serializable
@SerialName("Undefined")
data object UndefinedReference : Reference()

@Serializable
@SerialName("Business")
data class BusinessReference(val business: BusinessId) : Reference()

@Serializable
@SerialName("Character")
data class CharacterReference(val character: CharacterId) : Reference()

@Serializable
@SerialName("Culture")
data class CultureReference(val culture: CultureId) : Reference()

@Serializable
@SerialName("God")
data class GodReference(val god: GodId) : Reference()

@Serializable
@SerialName("Organization")
data class OrganizationReference(val organization: OrganizationId) : Reference()

@Serializable
@SerialName("Realm")
data class RealmReference(val realm: RealmId) : Reference()

@Serializable
@SerialName("Town")
data class TownReference(val town: TownId) : Reference()
