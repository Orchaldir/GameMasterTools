package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.canDeleteStreet(street: StreetId) = DeleteResult(street)
    .addElements(getBuildingsForStreet(street))
    .addElements(getTowns(street))

fun State.getStreets(position: Position) = when (position) {
    is InTownMap -> getStreets(position.townMap)
    else -> getStreetStorage().getAll()
}

fun State.getStreets(town: TownMapId) = getStreetStorage().get(getStreetIds(town))

fun State.getStreetIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.construction.getOptionalStreet() }
    .distinct()
