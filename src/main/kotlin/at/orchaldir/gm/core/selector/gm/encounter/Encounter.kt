package at.orchaldir.gm.core.selector.gm.encounter

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.gm.encounter.EncounterId
import at.orchaldir.gm.core.selector.world.getRegionsWithEncounter

fun State.canDeleteEncounter(encounter: EncounterId) = DeleteResult(encounter)
    .addElements(getEncountersWith(encounter))
    .addElements(getRegionsWithEncounter(encounter))

fun State.getEncountersWith(encounter: EncounterId) = getEncounterStorage()
    .getAll()
    .filter { it.entry.contains(encounter) }

fun State.getEncountersWith(template: CharacterTemplateId) = getEncounterStorage()
    .getAll()
    .filter { it.entry.contains(template) }

