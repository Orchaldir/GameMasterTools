package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.getBuildingAgeComparator

fun State.canDelete(building: Building) = building.ownership.current.canDelete()
        && getCharactersLivingIn(building.id).isEmpty()
        && getCharactersPreviouslyLivingIn(building.id).isEmpty()

fun State.countBuildings(town: TownId) = getBuildingStorage()
    .getAll()
    .count { it.lot.town == town }

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

fun State.getBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.lot.town == town }
