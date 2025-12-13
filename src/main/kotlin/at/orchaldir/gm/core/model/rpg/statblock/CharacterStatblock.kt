package at.orchaldir.gm.core.model.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CharacterStatblockType {
    Statblock,
    Template,
    ModifiedTemplate,
    Undefined,
}

@Serializable
sealed class CharacterStatblock {

    fun getType() = when (this) {
        UndefinedCharacterStatblock -> CharacterStatblockType.Undefined
        is UniqueCharacterStatblock -> CharacterStatblockType.Statblock
        is UseStatblockOfTemplate -> CharacterStatblockType.Template
        is ModifyStatblockOfTemplate -> CharacterStatblockType.ModifiedTemplate
    }

    fun getStatblock(state: State) = when (this) {
        UndefinedCharacterStatblock -> null
        is UniqueCharacterStatblock -> statblock
        is UseStatblockOfTemplate -> state.getCharacterTemplateStorage()
            .getOrThrow(template)
            .statblock

        is ModifyStatblockOfTemplate -> {
            val statblock = state.getCharacterTemplateStorage()
                .getOrThrow(template)
                .statblock

            update.resolve(statblock)
        }
    }

    fun calculateCost(state: State) = getStatblock(state)?.calculateCost(state)

    fun contains(statistic: StatisticId) = when (this) {
        UndefinedCharacterStatblock -> false
        is UniqueCharacterStatblock -> statblock.statistics.containsKey(statistic)
        is UseStatblockOfTemplate -> false
        is ModifyStatblockOfTemplate -> update.statistics.containsKey(statistic)
    }

    fun contains(template: CharacterTemplateId) = when (this) {
        UndefinedCharacterStatblock -> false
        is UniqueCharacterStatblock -> false
        is UseStatblockOfTemplate -> this.template == template
        is ModifyStatblockOfTemplate -> this.template == template
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
@SerialName("Modify")
data class ModifyStatblockOfTemplate(
    val template: CharacterTemplateId,
    val update: StatblockUpdate,
) : CharacterStatblock()

@Serializable
@SerialName("Undefined")
data object UndefinedCharacterStatblock : CharacterStatblock()