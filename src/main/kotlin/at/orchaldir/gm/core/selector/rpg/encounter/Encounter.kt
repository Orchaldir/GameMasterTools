package at.orchaldir.gm.core.selector.rpg.encounter

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.rpg.encounter.EncounterId

fun State.canDeleteEncounter(encounter: EncounterId) = DeleteResult(encounter)

fun State.getEncountersWith(template: CharacterTemplateId) = getEncounterStorage()
    .getAll()
    .filter { it.entry.contains(template) }

