package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BuilderType {
    Undefined,
    BuildByBusiness,
    BuildByCharacter,
}

@Serializable
sealed class Builder {

    fun getType() = when (this) {
        is UndefinedBuilder -> BuilderType.Undefined
        is BuildByBusiness -> BuilderType.BuildByBusiness
        is BuildByCharacter -> BuilderType.BuildByCharacter
    }

}

@Serializable
@SerialName("Undefined")
data object UndefinedBuilder : Builder()

@Serializable
@SerialName("Business")
data class BuildByBusiness(val business: BusinessId) : Builder()

@Serializable
@SerialName("Character")
data class BuildByCharacter(val character: CharacterId) : Builder()



