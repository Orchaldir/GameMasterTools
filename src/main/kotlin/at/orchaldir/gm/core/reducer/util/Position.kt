package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.util.requireExists

fun checkPositionHistory(
    state: State,
    history: History<Position>,
    startDate: Date,
    allowedTypes: Collection<PositionType> = PositionType.entries,
) = checkHistory(state, history, startDate, "home") { state, param, position, date ->
    checkPosition(state, param, position, date, allowedTypes)
}

fun checkPosition(
    state: State,
    position: Position,
    noun: String,
    date: Date?,
    allowedTypes: Collection<PositionType>,
) {
    require(allowedTypes.contains(position.getType())) { "Position has invalid type ${position.getType()}!" }

    when (position) {
        UndefinedPosition -> return
        Homeless -> return
        is InApartment -> {
            val building = state
                .requireExists(state.getBuildingStorage(), position.building, date) { noun }

            if (building.purpose is ApartmentHouse) {
                require(position.apartmentIndex < building.purpose.apartments) { "The $noun's apartment index is too high!" }
            } else {
                error("The $noun is not an apartment house!")
            }
        }

        is InBuilding -> state.requireExists(state.getBuildingStorage(), position.building, date) { noun }
        is InDistrict -> state.requireExists(state.getDistrictStorage(), position.district, date) { noun }

        is InHome -> {
            val building = state
                .requireExists(state.getBuildingStorage(), position.building, date) { noun }

            require(building.purpose.isHome()) { "The $noun is not a home!" }
        }

        is InRealm -> state.requireExists(state.getRealmStorage(), position.realm, date) { noun }
        is InPlane -> state.getPlaneStorage().require(position.plane) { "Requires unknown $noun!" }
        is InTown -> state.requireExists(state.getTownStorage(), position.town, date) { noun }
        is InTownMap -> {
            val townMap = state.requireExists(state.getTownMapStorage(), position.townMap, date) { noun }
            require(position.tileIndex in 0..<townMap.map.size.tiles()) { "The $noun's tile index ${position.tileIndex} is outside the town map!" }
        }
    }
}