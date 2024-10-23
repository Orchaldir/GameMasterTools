package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.OwnedByCharacter
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun State.getAgeInYears(building: Building) = getDefaultCalendar()
    .getDurationInYears(building.constructionDate, time.currentDate)

fun State.canDelete(building: Building) = building.ownership.owner.canDelete()

fun State.getEarliestBuilding(buildings: List<Building>): Building? {
    val calendar = getDefaultCalendar()
    val lengthComparator =
        Comparator<Building> { a: Building, b: Building -> calendar.compareTo(a.constructionDate, b.constructionDate) }

    return buildings.minWithOrNull(lengthComparator)
}

fun State.getBuildings(style: ArchitecturalStyleId) = getBuildingStorage()
    .getAll()
    .filter { it.architecturalStyle == style }

fun State.getBuildings(town: TownId) = getBuildingStorage().getAll()
    .filter { it.lot.town == town }

fun State.getOwnedBuildings(character: CharacterId) = getBuildingStorage().getAll()
    .filter { it.ownership.owner is OwnedByCharacter && it.ownership.owner.character == character }

fun State.getPreviouslyOwnedBuildings(character: CharacterId) = getBuildingStorage().getAll()
    .filter { it.ownership.contains(character) }

