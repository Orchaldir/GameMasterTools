package at.orchaldir.gm.core.model.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StatblockLookupType {
    Unique,
    UseTemplate,
    ModifyTemplate,
    Undefined,
}

@Serializable
sealed class StatblockLookup {

    fun getType() = when (this) {
        UndefinedStatblockLookup -> StatblockLookupType.Undefined
        is UniqueStatblock -> StatblockLookupType.Unique
        is UseStatblockOfTemplate -> StatblockLookupType.UseTemplate
        is ModifyStatblockOfTemplate -> StatblockLookupType.ModifyTemplate
    }

    fun calculateCost(raceId: RaceId, state: State) = state.getStatblock(raceId, this).calculateCost(state)


    fun contains(statistic: StatisticId) = when (this) {
        UndefinedStatblockLookup -> false
        is UniqueStatblock -> statblock.statistics.containsKey(statistic)
        is UseStatblockOfTemplate -> false
        is ModifyStatblockOfTemplate -> update.statistics.containsKey(statistic)
    }

    fun contains(template: CharacterTemplateId) = when (this) {
        UndefinedStatblockLookup -> false
        is UniqueStatblock -> false
        is UseStatblockOfTemplate -> this.template == template
        is ModifyStatblockOfTemplate -> this.template == template
    }
}

@Serializable
@SerialName("Unique")
data class UniqueStatblock(
    val statblock: StatblockUpdate,
) : StatblockLookup()

@Serializable
@SerialName("Template")
data class UseStatblockOfTemplate(
    val template: CharacterTemplateId,
) : StatblockLookup()

@Serializable
@SerialName("Modify")
data class ModifyStatblockOfTemplate(
    val template: CharacterTemplateId,
    val update: StatblockUpdate,
) : StatblockLookup()

@Serializable
@SerialName("Undefined")
data object UndefinedStatblockLookup : StatblockLookup()