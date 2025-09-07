package at.orchaldir.gm.core.selector.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.PersonalityTraitId
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.updateBeard
import at.orchaldir.gm.core.model.character.appearance.updateHairColor
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.isIn
import at.orchaldir.gm.core.model.util.origin.BornElement
import at.orchaldir.gm.core.model.util.wasIn
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.culture.getKnownLanguages
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.realm.countBattlesLedBy
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.isCreator
import at.orchaldir.gm.core.selector.util.isCurrentOrFormerOwner
import at.orchaldir.gm.core.selector.world.getCurrentTownMap
import at.orchaldir.gm.utils.Id
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
    .groupingBy { it.vitalStatus.getCauseOfDeath()?.getType()!! }
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

fun <ID : Id<ID>> State.countKilledCharacters(id: ID) = getCharacterStorage()
    .getAll()
    .count { it.vitalStatus.isDestroyedBy(id) }

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

fun State.getSecretIdentitiesOf(character: CharacterId) = getCharacterStorage()
    .getAll()
    .filter { it.authenticity.isSecretIdentityOf(character) }

// housing status

fun <ID : Id<ID>> State.getCharactersLivingIn(id: ID) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.isIn(id) }

fun <ID : Id<ID>> State.getCharactersLivingIn(ids: Collection<ID>) = getCharacterStorage()
    .getAll()
    .filter { ids.any { id -> it.housingStatus.isIn(id) } }

fun State.getCharactersLivingInApartment(building: BuildingId, apartment: Int) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.current.isInApartment(building, apartment) }

fun State.getCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.current.isInBuilding(building) }

fun State.countCharactersLivingInHouse(building: BuildingId) = getCharacterStorage()
    .getAll()
    .count { it.housingStatus.current.isInBuilding(building) }

fun <ID : Id<ID>> State.getCharactersPreviouslyLivingIn(id: ID) = getCharacterStorage()
    .getAll()
    .filter { it.housingStatus.wasIn(id) }

fun <ID : Id<ID>> State.getCharactersPreviouslyLivingIn(ids: Collection<ID>) = getCharacterStorage()
    .getAll()
    .filter { ids.any { id -> it.housingStatus.wasIn(id) } }

fun State.getResidents(town: TownId?, townMap: TownMapId?): List<Character> {
    val residents = if (town != null) {
        getCharactersLivingIn(town)
    } else {
        emptyList()
    }

    return if (townMap != null) {
        residents + getCharactersLivingIn(townMap)
    } else {
        residents
    }
}

fun State.isResident(character: Character, town: TownMapId) = character.housingStatus.current.getBuilding()
    ?.let { getBuildingStorage().getOrThrow(it).position.isIn(town) }
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

fun State.isWorkingIn(character: Character, town: TownMapId) = getBusinessStorage()
    .getOptional(character.getBusiness())
    ?.position?.isIn(town)
    ?: false

// get relatives

fun State.getParents(id: CharacterId): List<Character> {
    val storage = getCharacterStorage()
    val character = storage.get(id) ?: return listOf()

    return when (character.origin) {
        is BornElement -> listOfNotNull(character.origin.father, character.origin.mother)
            .map { storage.getOrThrow(CharacterId(it)) }

        else -> listOf()
    }
}

fun Character.getFather() = when (origin) {
    is BornElement -> origin.father?.let { CharacterId(it) }
    else -> null
}

fun Character.getMother() = when (origin) {
    is BornElement -> origin.mother?.let { CharacterId(it) }
    else -> null
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
        is BornElement -> it.origin.isChildOf(id.value)
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