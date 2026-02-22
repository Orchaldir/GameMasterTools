package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InSettlementMap
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId

fun State.canDeleteStreet(street: StreetId) = DeleteResult(street)
    .addElements(getBuildingsForStreet(street))
    .addElements(getSettlementMaps(street))

fun State.getStreets(position: Position) = when (position) {
    is InSettlementMap -> getStreets(position.map)
    else -> getStreetStorage().getAll()
}

fun State.getStreets(town: SettlementMapId) = getStreetStorage().get(getStreetIds(town))

fun State.getStreetIds(town: SettlementMapId) = getSettlementMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.construction.getOptionalStreet() }
    .distinct()
