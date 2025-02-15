package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.language.InventedLanguage
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.CreatedRace
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.utils.Id

fun State.canDelete(race: RaceId) = getRaceStorage().getSize() > 1 && getCharacters(race).isEmpty()

fun countEachRace(characters: Collection<Character>) = characters
    .groupingBy { it.race }
    .eachCount()

fun State.getRaces(id: RaceAppearanceId) = getRaceStorage().getAll()
    .filter { it.lifeStages.contains(id) }

fun State.getPossibleParents(race: RaceId) = getRaceStorage()
    .getAllExcept(race)

fun <ID : Id<ID>> State.getRacesCreatedBy(id: ID) = getRaceStorage()
    .getAll().filter { l ->
        when (l.origin) {
            is CreatedRace -> l.origin.inventor.isId(id)
            else -> false
        }
    }