package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getCharactersLivingIn
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.getAgeInYears(building: Building) = getDefaultCalendar()
    .getDurationInYears(building.constructionDate, time.currentDate)

fun State.canDelete(building: Building) = building.ownership.owner.canDelete() &&
        getCharactersLivingIn(building.id).isEmpty()

fun countPurpose(buildings: Collection<Building>) = buildings
    .groupingBy { it.purpose.getType() }
    .eachCount()

fun State.getMinNumberOfApartment(building: BuildingId) =
    (getCharactersLivingIn(building)
        .mapNotNull { it.livingStatus.getApartmentIndex() }
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
    .filter { it.architecturalStyle == style }

fun State.getBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.lot.town == town }

fun State.getOwnedBuildings(character: CharacterId) = getBuildingStorage().getAll()
    .filter { it.ownership.owner is OwnedByCharacter && it.ownership.owner.character == character }

fun State.getPreviouslyOwnedBuildings(character: CharacterId) = getBuildingStorage().getAll()
    .filter { it.ownership.contains(character) }

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

