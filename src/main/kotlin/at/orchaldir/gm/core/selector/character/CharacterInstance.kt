package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterInstanceId
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.race.RaceId

fun State.canDeleteCharacterInstance(instance: CharacterInstanceId) = DeleteResult(instance)

fun State.getCharacterInstances(race: RaceId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.race == race }

fun State.getCharacterInstances(template: CharacterTemplateId) = getCharacterInstanceStorage()
    .getAll()
    .filter { it.basedOn.isId(template) }
