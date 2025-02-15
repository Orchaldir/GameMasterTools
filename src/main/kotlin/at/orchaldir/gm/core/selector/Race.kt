package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId

fun State.canDelete(race: RaceId) = getRaceStorage().getSize() > 1 && getCharacters(race).isEmpty()

fun countEachRace(characters: Collection<Character>) = characters
    .groupingBy { it.race }
    .eachCount()

fun State.getRaces(id: RaceAppearanceId) = getRaceStorage().getAll()
    .filter { it.lifeStages.contains(id) }

fun State.getPossibleParents(race: RaceId) = getRaceStorage()
    .getAllExcept(race)