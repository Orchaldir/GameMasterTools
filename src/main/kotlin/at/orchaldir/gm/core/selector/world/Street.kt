package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Homeless
import at.orchaldir.gm.core.model.util.InApartment
import at.orchaldir.gm.core.model.util.InDistrict
import at.orchaldir.gm.core.model.util.InHouse
import at.orchaldir.gm.core.model.util.InPlane
import at.orchaldir.gm.core.model.util.InRealm
import at.orchaldir.gm.core.model.util.InTown
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.UndefinedPosition
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownMapId

fun State.canDelete(street: StreetId) = getTowns(street).isEmpty()

fun State.getStreets(position: Position) = when (position) {
    is InTownMap -> getStreets(position.townMap)
    else -> getStreetStorage().getAll()
}

fun State.getStreets(town: TownMapId) = getStreetIds(town)
    .map { getStreetStorage().getOrThrow(it) }

fun State.getStreetIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.construction.getOptionalStreet() }
    .distinct()
