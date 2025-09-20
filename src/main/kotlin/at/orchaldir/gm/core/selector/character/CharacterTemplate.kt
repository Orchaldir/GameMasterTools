package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.race.RaceId

fun State.canDeleteCharacterTemplate(template: CharacterTemplateId) = DeleteResult(template)

// get characters

fun State.getCharacterTemplates(culture: CultureId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.culture == culture }

fun State.getCharacterTemplates(race: RaceId) = getCharacterTemplateStorage()
    .getAll()
    .filter { it.race == race }
