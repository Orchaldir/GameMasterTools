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

enum class CreatorType {
    Undefined,
    CreatedByBusiness,
    CreatedByCharacter,
    CreatedByCulture,
    CreatedByGod,
    CreatedByOrganization,
    CreatedByRealm,
    CreatedByTown,
}

@Serializable
sealed class Creator {

    fun getType() = when (this) {
        is UndefinedCreator -> CreatorType.Undefined
        is CreatedByBusiness -> CreatorType.CreatedByBusiness
        is CreatedByCharacter -> CreatorType.CreatedByCharacter
        is CreatedByCulture -> CreatorType.CreatedByCulture
        is CreatedByGod -> CreatorType.CreatedByGod
        is CreatedByOrganization -> CreatorType.CreatedByOrganization
        is CreatedByRealm -> CreatorType.CreatedByRealm
        is CreatedByTown -> CreatorType.CreatedByTown
    }

    fun <ID : Id<ID>> isId(id: ID) = when (this) {
        UndefinedCreator -> false
        is CreatedByBusiness -> business == id
        is CreatedByCharacter -> character == id
        is CreatedByCulture -> culture == id
        is CreatedByGod -> god == id
        is CreatedByOrganization -> organization == id
        is CreatedByRealm -> realm == id
        is CreatedByTown -> town == id
    }

}

@Serializable
@SerialName("Undefined")
data object UndefinedCreator : Creator()

@Serializable
@SerialName("Business")
data class CreatedByBusiness(val business: BusinessId) : Creator()

@Serializable
@SerialName("Character")
data class CreatedByCharacter(val character: CharacterId) : Creator()

@Serializable
@SerialName("Culture")
data class CreatedByCulture(val culture: CultureId) : Creator()

@Serializable
@SerialName("God")
data class CreatedByGod(val god: GodId) : Creator()

@Serializable
@SerialName("Organization")
data class CreatedByOrganization(val organization: OrganizationId) : Creator()

@Serializable
@SerialName("Realm")
data class CreatedByRealm(val realm: RealmId) : Creator()

@Serializable
@SerialName("Town")
data class CreatedByTown(val town: TownId) : Creator()

interface ComplexCreation {
    fun <ID : Id<ID>> isCreatedBy(id: ID): Boolean
}

interface Creation : ComplexCreation {
    fun creator(): Creator
    override fun <ID : Id<ID>> isCreatedBy(id: ID) = creator().isId(id)
}



