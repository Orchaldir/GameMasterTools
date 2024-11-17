package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CreatorType {
    Undefined,
    CreatedByBusiness,
    CreatedByCharacter,
}

@Serializable
sealed class Creator {

    fun getType() = when (this) {
        is UndefinedCreator -> CreatorType.Undefined
        is CreatedByBusiness -> CreatorType.CreatedByBusiness
        is CreatedByCharacter -> CreatorType.CreatedByCharacter
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



