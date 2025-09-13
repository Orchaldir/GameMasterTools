package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.util.canDeletePosition
import at.orchaldir.gm.core.selector.util.countBuildingsIn
import at.orchaldir.gm.core.selector.util.getBuildingAgeComparator

fun State.canDeleteBuilding(building: BuildingId) = DeleteResult(building)
    .apply { canDeletePosition(building, it) }

fun State.countBuildings(townId: TownId): Int {
    val countInTownMap = getCurrentTownMap(townId)?.let { countBuildingsIn(it.id) } ?: 0

    return countInTownMap + countBuildingsIn(townId)
}

fun countEachPurpose(buildings: Collection<Building>) = buildings
    .groupingBy { it.purpose.getType() }
    .eachCount()

fun State.getMinNumberOfApartment(building: BuildingId) =
    (getCharactersLivingIn(building)
        .mapNotNull { it.housingStatus.current.getApartmentIndex() }
        .maxOrNull() ?: 1) + 1

fun State.getEarliestBuilding(buildings: List<Building>) =
    buildings.minWithOrNull(getBuildingAgeComparator())

fun State.getApartmentHouses() = getBuildingStorage()
    .getAll()
    .filter { it.purpose is ApartmentHouse }

fun State.getHomes() = getBuildingStorage()
    .getAll()
    .filter { it.purpose.isHome() }

fun State.getBuildings(style: ArchitecturalStyleId) = getBuildingStorage()
    .getAll()
    .filter { it.style == style }

fun State.getBuildingsForStreet(street: StreetId) = getBuildingStorage()
    .getAll()
    .filter { it.address.contains(street) }

