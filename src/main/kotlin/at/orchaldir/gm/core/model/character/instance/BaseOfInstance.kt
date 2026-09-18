package at.orchaldir.gm.core.model.character.instance

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BaseOfInstanceType {
    Character,
    Template,
}

@Serializable
sealed class BaseOfInstance {

    fun getType() = when (this) {
        is BasedOnCharacter -> BaseOfInstanceType.Character
        is BasedOnTemplate -> BaseOfInstanceType.Template
    }

}

@Serializable
@SerialName("Character")
data class BasedOnCharacter(
    val character: CharacterId,
) : BaseOfInstance()

@Serializable
@SerialName("Template")
data class BasedOnTemplate(
    val template: CharacterTemplateId,
) : BaseOfInstance()
