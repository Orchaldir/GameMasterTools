package at.orchaldir.gm.core.model.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.RaceLookup
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
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

    fun template() = when (this) {
        UndefinedStatblockLookup -> null
        is UniqueStatblock -> null
        is UseStatblockOfTemplate -> this.template
        is ModifyStatblockOfTemplate -> this.template
    }

    fun hasTemplate() = when (this) {
        UndefinedStatblockLookup -> false
        is UniqueStatblock -> false
        is UseStatblockOfTemplate -> true
        is ModifyStatblockOfTemplate -> true
    }

    fun calculateCost(raceId: RaceId, state: State) = state.getStatblock(raceId, this)
        .calculateCost(state)

    fun calculateCost(lookup: RaceLookup, state: State) = state
        .getStatblock(lookup, this)
        .calculateCost(state)


    fun contains(statistic: StatisticId) = when (this) {
        UndefinedStatblockLookup -> false
        is UniqueStatblock -> statblock.statistics.containsKey(statistic)
        is UseStatblockOfTemplate -> false
        is ModifyStatblockOfTemplate -> update.statistics.containsKey(statistic)
    }

    fun contains(template: CharacterTemplateId) = template() == template

    fun contains(state: State, race: RaceId, trait: CharacterTraitId) =
        contains(trait) || state.getStatblock(race, this).traits.contains(trait)

    fun contains(state: State, race: RaceLookup, trait: CharacterTraitId) =
        contains(trait) || state.getStatblock(race, this).traits.contains(trait)

    private fun contains(trait: CharacterTraitId): Boolean = when (this) {
        UndefinedStatblockLookup -> false
        is UniqueStatblock -> statblock.contains(trait)
        is UseStatblockOfTemplate -> false
        is ModifyStatblockOfTemplate -> update.contains(trait)
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