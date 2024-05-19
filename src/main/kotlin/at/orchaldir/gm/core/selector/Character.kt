package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.character.RaceId
import at.orchaldir.gm.core.model.language.LanguageId

fun State.getCharacters(culture: CultureId) = characters.getAll().filter { c -> c.culture == culture }

fun State.getCharacters(language: LanguageId) = characters.getAll().filter { c -> c.languages.containsKey(language) }

fun State.getCharacters(trait: PersonalityTraitId) = characters.getAll().filter { c -> c.personality.contains(trait) }

fun State.getCharacters(race: RaceId) = characters.getAll().filter { c -> c.race == race }