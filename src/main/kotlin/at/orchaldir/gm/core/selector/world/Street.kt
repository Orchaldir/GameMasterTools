package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TownId

fun State.canDelete(street: StreetId) = getTowns(street).isEmpty()

fun State.getStreets(town: TownId) = getTownStorage().getOrThrow(town)
    .map.tiles.mapNotNull { it.construction.getStreet() }.distinct()

