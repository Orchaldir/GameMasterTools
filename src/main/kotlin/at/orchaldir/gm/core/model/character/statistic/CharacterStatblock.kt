package at.orchaldir.gm.core.model.character.statistic

import at.orchaldir.gm.core.model.character.CharacterTemplateId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CharacterStatblockType {
    Statblock,
    Template,
    Undefined,
}

@Serializable
sealed class CharacterStatblock {

    fun getType() = when (this) {
        UndefinedCharacterStatblock -> CharacterStatblockType.Undefined
        is UniqueCharacterStatblock -> CharacterStatblockType.Statblock
        is UseStatblockOfTemplate -> CharacterStatblockType.Template
    }
}

@Serializable
@SerialName("Statblock")
data class UniqueCharacterStatblock(
    val statblock: Statblock,
) : CharacterStatblock()

@Serializable
@SerialName("Template")
data class UseStatblockOfTemplate(
    val template: CharacterTemplateId,
) : CharacterStatblock()

@Serializable
@SerialName("Undefined")
data object UndefinedCharacterStatblock : CharacterStatblock()