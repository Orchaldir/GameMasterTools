package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasPosition
import at.orchaldir.gm.core.model.util.InDistrict
import at.orchaldir.gm.core.model.util.InPlane
import at.orchaldir.gm.core.model.util.InRealm
import at.orchaldir.gm.core.model.util.InTown
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun State.getBuildingsForPosition(position: Position) = getHasPositionsForPosition(getBuildingStorage(), position)

fun <ID : Id<ID>, ELEMENT> getHasPositionsForPosition(
    storage: Storage<ID, ELEMENT>,
    position: Position,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPosition = when (position) {
    is InDistrict -> getHasPositions(storage, position.district)
    is InPlane -> getHasPositions(storage, position.plane)
    is InRealm -> getHasPositions(storage, position.realm)
    is InTown -> getHasPositions(storage, position.town)
    is InTownMap -> getHasPositions(storage, position.townMap)
    else -> error("House Number is not supported by Position type ${position.getType()}!")
}


fun <ID : Id<ID>> State.getBuildings(id: ID) = getHasPositions(getBuildingStorage(), id)

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT> getHasPositions(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where
        ELEMENT : Element<ID0>,
        ELEMENT : HasPosition = storage
    .getAll()
    .filter { it.position().isIn(id) }