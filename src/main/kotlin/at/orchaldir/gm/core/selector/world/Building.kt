package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.util.InDistrict
import at.orchaldir.gm.core.model.util.InPlane
import at.orchaldir.gm.core.model.util.InRealm
import at.orchaldir.gm.core.model.util.InTown
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.canDelete
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.getBuildingAgeComparator
import at.orchaldir.gm.utils.Id

fun State.canDelete(building: Building) = building.ownership.canDelete()
        && getCharactersLivingIn(building.id).isEmpty()
        && getCharactersPreviouslyLivingIn(building.id).isEmpty()

fun State.countBuildings(townId: TownId): Int {
    val countInTownMap = getCurrentTownMap(townId)?.let { countBuildings(it.id) } ?: 0

    return countInTownMap + countBuildings(townId)
}

fun <ID : Id<ID>> State.countBuildings(id: ID) = getBuildingStorage()
    .getAll()
    .count { it.position.isIn(id) }

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

fun State.getBuilding(business: BusinessId) = getBuildingStorage().getAll()
    .firstOrNull { it.purpose.contains(business) }

fun State.getBuildings(style: ArchitecturalStyleId) = getBuildingStorage()
    .getAll()
    .filter { it.style == style }

fun <ID : Id<ID>> State.getBuildings(id: ID) = getBuildingStorage()
    .getAll()
    .filter { it.position.isIn(id) }

fun State.getBuildingsForPosition(position: Position) = when (position) {
    is InDistrict -> getBuildings(position.district)
    is InPlane -> getBuildings(position.plane)
    is InRealm -> getBuildings(position.realm)
    is InTown -> getBuildings(position.town)
    is InTownMap -> getBuildings(position.townMap)
    else -> error("House Number is not supported by Position type ${position.getType()}!")
}
