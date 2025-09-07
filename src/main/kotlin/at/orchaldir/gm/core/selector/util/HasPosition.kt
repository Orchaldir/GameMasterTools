package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

// delete

fun <ID : Id<ID>> State.canDeleteWithPositions(id: ID, result: DeleteResult) = result
    .addElements(getBuildingsIn(id))
    .addElements(getBusinessesIn(id))
    .addElements(getRegionsIn(id))

// count

fun <ID : Id<ID>> State.countBuildingsIn(id: ID) = countHasPositionsIn(getBuildingStorage(), id)
fun <ID : Id<ID>> State.countBusinessesIn(id: ID) = countHasPositionsIn(getBusinessStorage(), id)

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT> countHasPositionsIn(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where
        ELEMENT : Element<ID0>,
        ELEMENT : HasPosition = storage
    .getAll()
    .count { it.position().isIn(id) }

// get

fun <ID : Id<ID>> State.hasNoHasPositionsIn(id: ID) = getBuildingsIn(id).isEmpty()
        && getBusinessesIn(id).isEmpty()
        && getRegionsIn(id).isEmpty()

fun State.getBuildingsForPosition(position: Position) = getHasPositionsForPosition(getBuildingStorage(), position)

fun <ID : Id<ID>, ELEMENT> getHasPositionsForPosition(
    storage: Storage<ID, ELEMENT>,
    position: Position,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasPosition = when (position) {
    is InDistrict -> getHasPositionsIn(storage, position.district)
    is InPlane -> getHasPositionsIn(storage, position.plane)
    is InRealm -> getHasPositionsIn(storage, position.realm)
    is InTown -> getHasPositionsIn(storage, position.town)
    is InTownMap -> getHasPositionsIn(storage, position.townMap)
    else -> error("House Number is not supported by Position type ${position.getType()}!")
}


fun <ID : Id<ID>> State.getBuildingsIn(id: ID) = getHasPositionsIn(getBuildingStorage(), id)
fun <ID : Id<ID>> State.getBusinessesIn(id: ID) = getHasPositionsIn(getBusinessStorage(), id)
fun <ID : Id<ID>> State.getRegionsIn(id: ID) = getHasPositionsIn(getRegionStorage(), id)

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT> getHasPositionsIn(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where
        ELEMENT : Element<ID0>,
        ELEMENT : HasPosition = storage
    .getAll()
    .filter { it.position().isIn(id) }