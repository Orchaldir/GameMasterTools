package at.orchaldir.gm.core.model.rpg.encounter

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.rpg.dice.NotRandomNumber
import at.orchaldir.gm.core.model.rpg.dice.RandomNumber
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EncounterEntryType {
    None,
    CharacterTemplate,
    Lookup,
    Combined,
    Table,
}

@Serializable
sealed class EncounterEntry {

    fun getType() = when (this) {
        NoEncounter -> EncounterEntryType.None
        is EncounterLookup -> EncounterEntryType.Lookup
        is CharacterTemplateEncounter -> EncounterEntryType.CharacterTemplate
        is CombinedEncounter -> EncounterEntryType.Combined
        is EncounterTable -> EncounterEntryType.Table
    }

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        NoEncounter -> false
        is EncounterLookup -> encounter == id
        is CharacterTemplateEncounter -> template == id
        is CombinedEncounter -> list.any { it.contains(id) }
        is EncounterTable -> table.entries.any { it.value.contains(id) }
    }

    fun validate(state: State, id: EncounterId?): Unit = when (this) {
        NoEncounter -> doNothing()
        is EncounterLookup -> {
            state.getEncounterStorage().require(encounter)
            require(id != encounter) { "Cannot be based on itself!" }
        }

        is CharacterTemplateEncounter -> state.getCharacterTemplateStorage().require(template)
        is CombinedEncounter -> list.forEach { it.validate(state, id) }
        is EncounterTable -> table.entries.forEach { it.value.validate(state, id) }
    }
}

@Serializable
@SerialName("None")
data object NoEncounter : EncounterEntry()

@Serializable
@SerialName("Template")
data class CharacterTemplateEncounter(
    val amount: RandomNumber,
    val template: CharacterTemplateId,
) : EncounterEntry() {

    constructor(template: CharacterTemplateId) : this(NotRandomNumber(1), template)

}

@Serializable
@SerialName("Lookup")
data class EncounterLookup(
    val encounter: EncounterId,
) : EncounterEntry()

@Serializable
@SerialName("Combined")
data class CombinedEncounter(
    val list: List<EncounterEntry>,
) : EncounterEntry()

@Serializable
@SerialName("Table")
data class EncounterTable(
    val table: Lookup<EncounterEntry>,
) : EncounterEntry()
