package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.updateBeard
import at.orchaldir.gm.core.model.character.appearance.updateHairColor
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.getKnownLanguages
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.realm.countBattlesLedBy
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner
import at.orchaldir.gm.core.selector.world.getCurrentTownMap
import at.orchaldir.gm.utils.math.unit.Distance

fun State.canCreateCharacter() = getCultureStorage().getSize() > 0

fun State.canDeleteCharacter(character: CharacterId) = getChildren(character).isEmpty()
        && getParents(character).isEmpty()
        && !isCurrentOrFormerOwner(character)
        && !isCreator(character)
        && getOrganizations(character).isEmpty()
        && countBattlesLedBy(character) == 0

// count

fun State.countCharacters(language: LanguageId) = getCharacterStorage()
    .getAll()
    .count { getKnownLanguages(it).containsKey(language) }

fun State.countCharactersWithJob(job: JobId) = getCharacterStorage()
    .getAll()
    .count { it.checkEmploymentStatus { it.hasJob(job) } }

fun State.countCharactersWithCurrentOrFormerJob(job: JobId) = getCharacterStorage()
    .getAll()
    .count { it.checkCurrentOrPreviousEmploymentStatus { it.hasJob(job) } }

fun State.countCharacters(race: RaceId) = getCharacterStorage()
    .getAll()
    .count { it.race == race }

fun State.countCharacters(title: TitleId) = getCharacterStorage()
    .getAll()
    .count { it.title == title }

fun State.countCurrentOrFormerEmployees(realm: RealmId) = getCharacterStorage()
    .getAll()
    .count { it.checkCurrentOrPreviousEmploymentStatus { it.isEmployedAt(realm) } }

fun State.countCurrentOrFormerEmployees(town: TownId) = getCharacterStorage()
    .getAll()
    .count { it.checkCurrentOrPreviousEmploymentStatus { it.isEmployedAt(town) } }

fun State.countEmployees(town: TownId) = getCharacterStorage()
    .getAll()
    .count { it.checkEmploymentStatus { it.isEmployedAt(town) } }

fun State.countResident(townId: TownId): Int {
    val townMap = getCurrentTownMap(townId)
        ?: return 0

    return countResident(townMap.id)
}

fun State.countResident(town: TownMapId) = getCharacterStorage()
    .getAll()
    .count { isResident(it, town) }

// count each

fun countEachCauseOfDeath(characters: Collection<Character>) = characters
    .filter { it.vitalStatus is Dead }
    .groupingBy { it.vitalStatus.getCauseOfDeath()!! }
    .eachCount()

fun countEachCulture(characters: Collection<Character>) = characters
    .groupingBy { it.culture }
    .eachCount()

fun countEachGender(characters: Collection<Character>) = characters
    .groupingBy { it.gender }
    .eachCount()

fun countEachSexualOrientation(characters: Collection<Character>) = characters
    .groupingBy { it.sexuality }
    .eachCount()

fun countEachHousingStatus(characters: Collection<Character>) = characters
    .groupingBy { it.housingStatus.current.getType() }
    .eachCount()

fun State.countEachLanguage(characters: Collection<Character>) = characters
    .flatMap { getKnownLanguages(it).keys }
    .groupingBy { it }
    .eachCount()

fun State.countCharactersKilledInBattle(battle: BattleId) = getCharacterStorage()
    .getAll()
    .count { it.vitalStatus.isCausedBy(battle) }

fun State.countCharactersKilledInCatastrophe(catastrophe: CatastropheId) = getCharacterStorage()
    .getAll()
    .count { it.vitalStatus.isCausedBy(catastrophe) }

fun State.countCharactersKilledInWar(war: WarId) = getCharacterStorage()
    .getAll()
    .count { it.vitalStatus.isCausedBy(war) }

// get characters

fun State.getCharacters(culture: CultureId) = getCharacterStorage()
    .getAll()
    .filter { it.culture == culture }

fun State.getCharacters(language: LanguageId) = getCharacterStorage()
    .getAll()
    .filter { c -> getKnownLanguages(c).containsKey(language) }

fun State.getCharacters(trait: PersonalityTraitId) = getCharacterStorage()
    .getAll()
    .filter { it.personality.contains(trait) }

fun State.getCharacters(race: RaceId) = getCharacterStorage()
    .getAll()
    .filter { it.race == race }

fun State.getCharacters(titleId: TitleId) = getCharacterStorage()
    .getAll()
    .filter { it.title == titleId }

// belief status

fun State.getBelievers(god: GodId) = getCharacterStorage()
    .getAll()
    .filter { it.beliefStatus.current.believesIn(god) }

fun State.getBelievers(pantheon: PantheonId) = getCharacterStorage()
    .getAll()
    .filter { it.beliefStatus.current.believesIn(pantheon) }

// housing status

fun State.getCharactersLivingIn(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.current.isLivingIn(building) }

fun State.getCharactersLivingInApartment(building: BuildingId, apartment: Int) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.current.isLivingInApartment(building, apartment) }

fun State.getCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.current.isLivingInHouse(building) }

fun State.countCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .count { it.housingStatus.current.isLivingInHouse(building) }

fun State.getCharactersPreviouslyLivingIn(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.previousEntries.any { it.entry.isLivingIn(building) } }

fun State.getResident(townId: TownId): List<Character> {
    val townMap = getCurrentTownMap(townId)
        ?: return emptyList()

    return getResident(townMap.id)
}

fun State.getResident(townMap: TownMapId) = getCharacterStorage()
    .getAll()
    .filter { isResident(it, townMap) }

fun State.isResident(character: Character, town: TownMapId) = character.housingStatus.current.getBuilding()
    ?.let { getBuildingStorage().getOrThrow(it).lot.town == town }
    ?: false

// employment status

fun State.getEmployees(job: JobId) = getCharacterStorage()
    .getAll()
    .filter { it.checkEmploymentStatus { it.hasJob(job) } }

fun State.getEmployees(business: BusinessId) = getCharacterStorage()
    .getAll()
    .filter { it.checkEmploymentStatus { it.isEmployedAt(business) } }

fun State.getEmployees(realm: RealmId) = getCharacterStorage()
    .getAll()
    .filter { it.checkEmploymentStatus { it.isEmployedAt(realm) } }

fun State.getEmployees(town: TownId) = getCharacterStorage()
    .getAll()
    .filter { it.checkEmploymentStatus { it.isEmployedAt(town) } }

fun State.getPreviousEmployees(job: JobId) = getCharacterStorage()
    .getAll()
    .filter { it.checkPreviousEmploymentStatus { it.hasJob(job) } }

fun State.getPreviousEmployees(business: BusinessId) = getCharacterStorage()
    .getAll()
    .filter { it.checkPreviousEmploymentStatus { it.isEmployedAt(business) } }

fun State.getPreviousEmployees(realm: RealmId) = getCharacterStorage()
    .getAll()
    .filter { it.checkPreviousEmploymentStatus { it.isEmployedAt(realm) } }

fun State.getWorkingIn(town: TownMapId) = getCharacterStorage()
    .getAll()
    .filter { isWorkingIn(it, town) }

fun State.isWorkingIn(character: Character, town: TownMapId) = character.getBusiness()
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

fun State.getOthersWithoutRelationship(character: Character) = getCharacterStorage()
    .getAllExcept(character.id)
    .filter { c -> !character.relationships.containsKey(c.id) }

// age

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
    val age = character.getAgeInYears(this)
    val race = getRaceStorage().getOrThrow(character.race)

    return scaleHeightByAge(race, height, age)
}

fun scaleHeightByAge(race: Race, height: Distance, age: Int): Distance {
    val relativeSize = race.lifeStages.getRelativeSize(age)

    return height * relativeSize
}

// appearance

fun State.getAppearanceForAge(character: Character): Appearance {
    val age = character.getAgeInYears(this)
    val race = getRaceStorage().getOrThrow(character.race)
    val height = scaleHeightByAge(race, character.appearance.getHeightFromSub(), age)

    return getAppearanceForAge(race, character.appearance, age, height)
}

fun getAppearanceForAge(race: Race, appearance: Appearance, age: Int): Appearance {
    val height = scaleHeightByAge(race, appearance.getHeightFromSub(), age)

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