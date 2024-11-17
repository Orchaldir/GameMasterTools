package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CreatorType {
    Undefined,
    BuildByBusiness,
    BuildByCharacter,
}

@Serializable
sealed class Creator {

    fun getType() = when (this) {
        is UndefinedCreator -> CreatorType.Undefined
        is BuildByBusiness -> CreatorType.BuildByBusiness
        is BuildByCharacter -> CreatorType.BuildByCharacter
    }

}

@Serializable
@SerialName("Undefined")
data object UndefinedCreator : Creator()

@Serializable
@SerialName("Business")
data class BuildByBusiness(val business: BusinessId) : Creator()

@Serializable
@SerialName("Character")
data class BuildByCharacter(val character: CharacterId) : Creator()



