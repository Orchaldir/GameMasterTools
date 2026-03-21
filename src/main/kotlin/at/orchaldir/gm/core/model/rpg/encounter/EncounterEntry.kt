package at.orchaldir.gm.core.model.rpg.encounter

import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.rpg.dice.RandomNumber
import at.orchaldir.gm.core.model.util.Lookup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EncounterEntryType {
    None,
    CharacterTemplate,
    Combined,
    Table,
}

@Serializable
sealed class EncounterEntry {

    fun getType() = when (this) {
        NoEncounter -> EncounterEntryType.None
        is CharacterTemplateEncounter -> EncounterEntryType.CharacterTemplate
        is CombinedEncounter -> EncounterEntryType.Combined
        is EncounterTable -> EncounterEntryType.Table
    }

    fun contains(id: CharacterTemplateId): Boolean = when (this) {
        NoEncounter -> false
        is CharacterTemplateEncounter -> template == id
        is CombinedEncounter -> list.any { it.contains(id) }
        is EncounterTable -> table.entries.any { it.value.contains(id) }
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
