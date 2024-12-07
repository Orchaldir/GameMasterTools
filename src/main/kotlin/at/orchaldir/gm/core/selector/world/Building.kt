package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.util.contains
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getCharactersLivingIn
import at.orchaldir.gm.core.selector.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.utils.Id

fun State.getAgeInYears(building: Building) = getDefaultCalendar()
    .getDurationInYears(building.constructionDate, time.currentDate)

fun State.canDelete(building: Building) = building.ownership.current.canDelete()
        && getCharactersLivingIn(building.id).isEmpty()
        && getCharactersPreviouslyLivingIn(building.id).isEmpty()

fun State.exists(id: BuildingId, date: Date) = exists(getBuildingStorage().getOrThrow(id), date)

fun State.exists(building: Building, date: Date) = getDefaultCalendar().compareTo(building.constructionDate, date) <= 0

fun countPurpose(buildings: Collection<Building>) = buildings
    .groupingBy { it.purpose.getType() }
    .eachCount()

fun State.getMinNumberOfApartment(building: BuildingId) =
    (getCharactersLivingIn(building)
        .mapNotNull { it.housingStatus.current.getApartmentIndex() }
        .maxOrNull() ?: 1) + 1

fun State.getEarliestBuilding(buildings: List<Building>) =
    buildings.minWithOrNull(getConstructionComparator())

fun State.getApartmentHouses() = getBuildingStorage()
    .getAll()
    .filter { it.purpose is ApartmentHouse }

fun State.getSingleFamilyHouses() = getBuildingStorage()
    .getAll()
    .filter { it.purpose is SingleFamilyHouse }

fun State.getBuilding(business: BusinessId) = getBuildingStorage().getAll()
    .firstOrNull { it.purpose.contains(business) }

fun State.getBuildings(style: ArchitecturalStyleId) = getBuildingStorage()
    .getAll()
    .filter { it.style == style }

fun State.getBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.lot.town == town }

// builder

fun <ID : Id<ID>> State.getBuildingsBuildBy(id: ID) = getBuildingStorage().getAll()
    .filter { it.builder.wasCreatedBy(id) }

// owner

fun State.getOwnedBuildings(character: CharacterId) = getBuildingStorage().getAll()
    .filter { it.ownership.current is OwnedByCharacter && it.ownership.current.character == character }

fun State.getPreviouslyOwnedBuildings(character: CharacterId) = getBuildingStorage().getAll()
    .filter { it.ownership.contains(character) }

fun State.getOwnedBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.ownership.current is OwnedByTown && it.ownership.current.town == town }

fun State.getPreviouslyOwnedBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.ownership.contains(town) }

// sort

enum class SortBuilding {
    Name,
    Construction,
}

fun State.getConstructionComparator(): Comparator<Building> {
    val calendar = getDefaultCalendar()
    return Comparator { a: Building, b: Building -> calendar.compareTo(a.constructionDate, b.constructionDate) }
}

fun State.getConstructionComparatorForPair(): Comparator<Pair<Building, String>> {
    val comparator = getConstructionComparator()
    return Comparator { a: Pair<Building, String>, b: Pair<Building, String> -> comparator.compare(a.first, b.first) }
}

fun State.sortBuildings(sort: SortBuilding = SortBuilding.Name) = sort(getBuildingStorage().getAll(), sort)

fun State.sort(buildings: Collection<Building>, sort: SortBuilding = SortBuilding.Name) = buildings
    .map { Pair(it, it.name(this)) }
    .sortedWith(when (sort) {
        SortBuilding.Name -> compareBy { it.second }
        SortBuilding.Construction -> getConstructionComparatorForPair()
    })

