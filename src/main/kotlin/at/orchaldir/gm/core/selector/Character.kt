package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.updateBeard
import at.orchaldir.gm.core.model.character.appearance.updateHairColor
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.economy.getOwnedBusinesses
import at.orchaldir.gm.core.selector.economy.getPreviouslyOwnedBusinesses
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.world.getOwnedBuildings
import at.orchaldir.gm.core.selector.world.getPreviouslyOwnedBuildings
import at.orchaldir.gm.utils.math.Distance

fun State.canCreateCharacter() = getCultureStorage().getSize() > 0

fun State.canDelete(character: CharacterId) = getChildren(character).isEmpty()
        && getParents(character).isEmpty()
        && getOwnedBuildings(character).isEmpty()
        && getPreviouslyOwnedBuildings(character).isEmpty()
        && getOwnedBusinesses(character).isEmpty()
        && getPreviouslyOwnedBusinesses(character).isEmpty()
        && !isCreator(character)

// count

fun State.countCharacters(language: LanguageId) = getCharacterStorage()
    .getAll()
    .count { c -> c.languages.containsKey(language) }

fun countEachCauseOfDeath(characters: Collection<Character>) = characters
    .filter { it.vitalStatus is Dead }
    .groupingBy { it.vitalStatus.getCauseOfDeath()!! }
    .eachCount()

fun countEachCulture(characters: Collection<Character>) = characters
    .groupingBy { it.culture }
    .eachCount()

fun countEachEmploymentStatus(characters: Collection<Character>) = characters
    .groupingBy { it.employmentStatus.current.getType() }
    .eachCount()

fun countEachGender(characters: Collection<Character>) = characters
    .groupingBy { it.gender }
    .eachCount()

fun countEachHousingStatus(characters: Collection<Character>) = characters
    .groupingBy { it.housingStatus.current.getType() }
    .eachCount()

fun countEachLanguage(characters: Collection<Character>) = characters
    .flatMap { it.languages.keys }
    .groupingBy { it }
    .eachCount()

fun countEachPersonality(characters: Collection<Character>) = characters
    .flatMap { it.personality }
    .groupingBy { it }
    .eachCount()

// get characters

fun State.getCharacters(culture: CultureId) = getCharacterStorage().getAll().filter { c -> c.culture == culture }

fun State.getCharacters(language: LanguageId) =
    getCharacterStorage().getAll().filter { c -> c.languages.containsKey(language) }

fun State.getCharacters(trait: PersonalityTraitId) =
    getCharacterStorage().getAll().filter { c -> c.personality.contains(trait) }

fun State.getCharacters(race: RaceId) = getCharacterStorage().getAll().filter { c -> c.race == race }

fun State.getOthers(id: CharacterId) = getCharacterStorage().getAll().filter { c -> c.id != id }

// housing status

fun State.getCharactersLivingIn(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.housingStatus.current.isLivingIn(building) }

fun State.getCharactersLivingInApartment(building: BuildingId, apartment: Int) = getCharacterStorage()
    .getAll()
    .filter { c -> c.housingStatus.current.isLivingInApartment(building, apartment) }

fun State.getCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.housingStatus.current.isLivingInHouse(building) }

fun State.countCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .count { c -> c.housingStatus.current.isLivingInHouse(building) }

fun State.getCharactersPreviouslyLivingIn(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.housingStatus.previousEntries.any { it.entry.isLivingIn(building) } }

fun State.getResident(town: TownId) = getCharacterStorage().getAll()
    .filter { isResident(it, town) }

fun State.isResident(character: Character, town: TownId) = character.housingStatus.current.getBuilding()
    ?.let { getBuildingStorage().getOrThrow(it).lot.town == town }
    ?: false

// employment status

fun State.getEmployees(job: JobId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.employmentStatus.current.hasJob(job) }

fun State.getEmployees(business: BusinessId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.employmentStatus.current.isEmployedAt(business) }

fun State.getPreviousEmployees(job: JobId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.employmentStatus.previousEntries.any { it.entry.hasJob(job) } }

fun State.getPreviousEmployees(business: BusinessId) = getCharacterStorage()
    .getAll()
    .filter { c -> c.employmentStatus.previousEntries.any { it.entry.isEmployedAt(business) } }

fun State.getWorkingIn(town: TownId) = getCharacterStorage()
    .getAll()
    .filter { isWorkingIn(it, town) }

fun State.isWorkingIn(character: Character, town: TownId) = character.employmentStatus.current.getBusiness()
    ?.let {
        getBuildingStorage().getAll().any { building -> building.purpose.contains(it) && building.lot.town == town }
    }
    ?: false

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

fun State.getAge(character: Character) = character.getAge(this, time.currentDate)

fun State.getAgeInYears(character: Character) = getDefaultCalendar().getYears(getAge(character))

fun State.isAlive(id: CharacterId, date: Date) = isAlive(getCharacterStorage().getOrThrow(id), date)

fun State.isAlive(character: Character, date: Date) = character
    .isAlive(getDefaultCalendar(), date)

fun State.getLiving(date: Date?) = if (date == null) {
    getCharacterStorage().getAll()
} else {
    getLiving(date)
}

fun State.getLiving(date: Date) = getCharacterStorage()
    .getAll()
    .filter { isAlive(it, date) }

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

// appearance

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

fun State.sortCharacters(sort: SortCharacter = SortCharacter.Name) =
    sortCharacters(getCharacterStorage().getAll(), sort)

fun State.sortCharacters(characters: Collection<Character>, sort: SortCharacter = SortCharacter.Name) = characters
    .map { Pair(it, it.name(this)) }
    .sortedWith(
        when (sort) {
            SortCharacter.Name -> compareBy { it.second }
            SortCharacter.Age -> getAgeComparatorForPair()
        })