package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.updateBeard
import at.orchaldir.gm.core.model.character.appearance.updateHairColor
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.world.SortBuilding
import at.orchaldir.gm.core.selector.world.getOwnedBuildings
import at.orchaldir.gm.core.selector.world.getConstructionComparatorForPair
import at.orchaldir.gm.core.selector.world.getPreviouslyOwnedBuildings
import at.orchaldir.gm.utils.math.Distance

fun State.canCreateCharacter() = getCultureStorage().getSize() > 0 && getCharacterStorage().getSize() > 0

fun State.canDelete(character: CharacterId) = getChildren(character).isEmpty()
        && getParents(character).isEmpty()
        && getInventedLanguages(character).isEmpty()
        && getOwnedBuildings(character).isEmpty()
        && getPreviouslyOwnedBuildings(character).isEmpty()

// count

fun countGender(characters: Collection<Character>) = characters
    .groupingBy { it.gender }
    .eachCount()

fun countLivingStatus(characters: Collection<Character>) = characters
    .groupingBy { it.livingStatus.getType() }
    .eachCount()

// get characters

fun State.getCharacters(culture: CultureId) = getCharacterStorage().getAll().filter { c -> c.culture == culture }

fun State.getCharacters(language: LanguageId) =
    getCharacterStorage().getAll().filter { c -> c.languages.containsKey(language) }

fun State.getCharacters(trait: PersonalityTraitId) =
    getCharacterStorage().getAll().filter { c -> c.personality.contains(trait) }

fun State.getCharacters(race: RaceId) = getCharacterStorage().getAll().filter { c -> c.race == race }

fun State.getOthers(id: CharacterId) = getCharacterStorage().getAll().filter { c -> c.id != id }

// living status

fun State.getCharactersLivingIn(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.livingStatus.isLivingIn(building) }

fun State.getCharactersLivingInApartment(building: BuildingId, apartment: Int) = getCharacterStorage()
    .getAll()
    .filter { c -> c.livingStatus.isLivingInApartment(building, apartment) }

fun State.getCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.livingStatus.isLivingInHouse(building) }

// get relatives

fun State.getParents(id: CharacterId): List<Character> {
    val character = getCharacterStorage().get(id) ?: return listOf()

    return when (character.origin) {
        is Born -> listOf(character.origin.father, character.origin.mother).map { getCharacterStorage().getOrThrow(it) }
        else -> listOf()
    }
}

fun Character.getFather() = when (origin) {
    is Born -> origin.father
    UndefinedCharacterOrigin -> null
}

fun Character.getMother() = when (origin) {
    is Born -> origin.mother
    UndefinedCharacterOrigin -> null
}

fun State.hasPossibleParents(id: CharacterId) =
    getPossibleFathers(id).isNotEmpty() && getPossibleMothers(id).isNotEmpty()

fun State.getPossibleFathers(id: CharacterId) = getCharacterStorage().getAll()
    .filter { it.gender == Gender.Male }
    .filter { it.id != id }

fun State.getPossibleMothers(id: CharacterId) = getCharacterStorage().getAll()
    .filter { it.gender == Gender.Female }
    .filter { it.id != id }

fun State.getChildren(id: CharacterId) = getCharacterStorage().getAll().filter {
    when (it.origin) {
        is Born -> it.origin.isParent(id)
        else -> false
    }
}

fun State.getSiblings(id: CharacterId): Set<Character> {
    val siblings = mutableSetOf<Character>()

    getParents(id).forEach { siblings.addAll(getChildren(it.id)) }
    siblings.removeIf { it.id == id }

    return siblings
}

// relationships

fun State.getOthersWithoutRelationship(character: Character) = getCharacterStorage().getAll()
    .filter { c -> c.id != character.id }
    .filter { c -> !character.relationships.containsKey(c.id) }

// age

fun State.getAge(id: CharacterId): Duration = getAge(getCharacterStorage().getOrThrow(id))

fun State.getAge(character: Character): Duration = character.getAge(time.currentDate)

fun State.getAgeInYears(character: Character) = getDefaultCalendar().getYears(getAge(character))

fun State.isAlive(id: CharacterId, date: Date) = isAlive(getCharacterStorage().getOrThrow(id), date)

fun State.isAlive(character: Character, date: Date) = getDefaultCalendar().compareTo(character.birthDate, date) <= 0

// height

fun State.scaleHeightByAge(character: Character, height: Distance): Distance {
    val age = getAgeInYears(character)
    val race = getRaceStorage().getOrThrow(character.race)

    return scaleHeightByAge(race, height, age)
}

fun scaleHeightByAge(race: Race, height: Distance, age: Int): Distance {
    val relativeSize = race.lifeStages.getRelativeSize(age)

    return height * relativeSize
}

fun State.getAppearanceForAge(character: Character): Appearance {
    val age = getAgeInYears(character)
    val race = getRaceStorage().getOrThrow(character.race)
    val height = scaleHeightByAge(race, character.appearance.getSize(), age)

    return getAppearanceForAge(race, character.appearance, age, height)
}

fun getAppearanceForAge(race: Race, appearance: Appearance, age: Int): Appearance {
    val height = scaleHeightByAge(race, appearance.getSize(), age)

    return getAppearanceForAge(race, appearance, age, height)
}

private fun getAppearanceForAge(race: Race, appearance: Appearance, age: Int, height: Distance): Appearance {
    var updatedAppearance = appearance.with(height)
    val stage = race.lifeStages.getLifeStage(age)

    if (stage != null) {
        if (!stage.hasBeard) {
            updatedAppearance = updateBeard(updatedAppearance, NoBeard)
        }

        if (stage.hairColor != null) {
            updatedAppearance = updateHairColor(updatedAppearance, stage.hairColor)
        }
    }

    return updatedAppearance
}

// sort

enum class SortCharacter {
    Name,
    Age,
}

fun State.getAgeComparator(): Comparator<Character> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Character, b: Character -> calendar.compareTo(a.birthDate, b.birthDate) }
}

fun State.getAgeComparatorForPair(): Comparator<Pair<Character, String>> {
    val comparator = getAgeComparator()
    return Comparator { a: Pair<Character, String>, b: Pair<Character, String> -> comparator.compare(a.first, b.first) }
}

fun State.sortBuildings(sort: SortCharacter = SortCharacter.Name) = sort(getCharacterStorage().getAll(), sort)

fun State.sort(buildings: Collection<Character>, sort: SortCharacter = SortCharacter.Name) = buildings
    .map { Pair(it, it.name(this)) }
    .sortedWith(when (sort) {
        SortCharacter.Name -> compareBy { it.second }
        SortCharacter.Age -> getAgeComparatorForPair()
    })