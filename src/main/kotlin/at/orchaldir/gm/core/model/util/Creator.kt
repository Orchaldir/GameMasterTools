package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.organization.OrganizationId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CreatorType {
    Undefined,
    CreatedByBusiness,
    CreatedByCharacter,
    CreatedByOrganization,
    CreatedByTown,
}

@Serializable
sealed class Creator {

    fun getType() = when (this) {
        is UndefinedCreator -> CreatorType.Undefined
        is CreatedByBusiness -> CreatorType.CreatedByBusiness
        is CreatedByCharacter -> CreatorType.CreatedByCharacter
        is CreatedByOrganization -> CreatorType.CreatedByOrganization
        is CreatedByTown -> CreatorType.CreatedByTown
    }

    fun <ID : Id<ID>> isId(id: ID) = when (this) {
        UndefinedCreator -> false
        is CreatedByBusiness -> business == id
        is CreatedByCharacter -> character == id
        is CreatedByOrganization -> organization == id
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
@SerialName("Organization")
data class CreatedByOrganization(val organization: OrganizationId) : Creator()

@Serializable
@SerialName("Town")
data class CreatedByTown(val town: TownId) : Creator()

interface Created {
    fun creator(): Creator
}



