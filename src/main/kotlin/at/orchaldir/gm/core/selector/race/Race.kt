package at.orchaldir.gm.core.selector.race

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.util.getExistingElements

fun State.canDelete(race: RaceId) = getRaceStorage().getSize() > 1 && getCharacters(race).isEmpty()

fun countEachRace(characters: Collection<Character>) = characters
    .groupingBy { it.race }
    .eachCount()

fun State.getExistingRaces(date: Date?) = getExistingElements(getRaceStorage().getAll(), date)

fun State.getRaces(id: RaceAppearanceId) = getRaceStorage().getAll()
    .filter { it.lifeStages.contains(id) }

fun State.getPossibleParents(race: RaceId) = getRaceStorage()
    .getAllExcept(race)
