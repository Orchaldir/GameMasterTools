package at.orchaldir.gm.core.model.rpg.encounter

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.RaceLookup
import at.orchaldir.gm.core.model.rpg.statblock.StatblockUpdate
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.rpg.trait.CharacterTraitId
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EncounterType {
    Unique,
    UseTemplate,
    ModifyTemplate,
    Undefined,
}

@Serializable
sealed class Encounter {

    fun getType() = when (this) {
        UndefinedEncounter -> EncounterType.Undefined
        is UniqueStatblock -> EncounterType.Unique
        is UseStatblockOfTemplate -> EncounterType.UseTemplate
        is ModifyStatblockOfTemplate -> EncounterType.ModifyTemplate
    }
}

@Serializable
@SerialName("Unique")
data class UniqueStatblock(
    val template: CharacterTemplateId,
) : Encounter()

@Serializable
@SerialName("Template")
data class UseStatblockOfTemplate(
    val template: CharacterTemplateId,
) : Encounter()

@Serializable
@SerialName("Modify")
data class ModifyStatblockOfTemplate(
    val template: CharacterTemplateId,
    val update: StatblockUpdate,
) : Encounter()

@Serializable
@SerialName("Undefined")
data object UndefinedEncounter : Encounter()