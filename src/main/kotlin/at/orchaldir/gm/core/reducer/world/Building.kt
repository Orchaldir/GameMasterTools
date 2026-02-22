package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.UpdateActionLot
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InSettlementMap
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.settlement.BuildingTile
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.getBuildingsForPosition
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_BUILDING: Reducer<AddBuilding, State> = { state, action ->
    val buildingId = state.getBuildingStorage().nextId
    val oldTownMap = state.getSettlementMapStorage().getOrThrow(action.settlement)
    val townMap = oldTownMap.build(action.tileIndex, action.size, BuildingTile(buildingId))
    val position = InSettlementMap(action.settlement, action.tileIndex)
    val building =
        Building(buildingId, position = position, size = action.size, constructionDate = state.getCurrentDate())

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().add(building),
                state.getSettlementMapStorage().update(townMap),
            )
        )
    )
}

fun deleteBuilding(state: State, id: BuildingId): Pair<State, List<Action>> {
    state.canDeleteBuilding(id).validate()

    val building = state.getBuildingStorage().getOrThrow(id)

    return if (building.position is InSettlementMap) {
        val oldTownMap = state.getSettlementMapStorage().getOrThrow(building.position.map)
        val townMap = oldTownMap.removeBuilding(building.id)

        noFollowUps(
            state.updateStorage(
                listOf(
                    state.getBuildingStorage().remove(id),
                    state.getSettlementMapStorage().update(townMap),
                )
            )
        )
    } else {
        noFollowUps(state.updateStorage(state.getBuildingStorage().remove(id)))
    }
}

fun updateBuilding(state: State, newBuilding: Building): Pair<State, List<Action>> {
    val oldBuilding = state.getBuildingStorage().getOrThrow(newBuilding.id)
    val updatedSettlementMaps = mutableListOf<SettlementMap>()

    newBuilding.validate(state)

    if (oldBuilding.position is InSettlementMap) {
        val oldTownMap = state.getSettlementMapStorage().getOrThrow(oldBuilding.position.map)

        if (newBuilding.position is InSettlementMap) {
            val newTownMap = state.getSettlementMapStorage().getOrThrow(newBuilding.position.map)

            if (newTownMap.id != oldTownMap.id) {
                updatedSettlementMaps.add(oldTownMap.removeBuilding(oldBuilding.id))
            }

            updatedSettlementMaps.add(
                oldTownMap.updateBuilding(
                    newBuilding.id,
                    newBuilding.position.tileIndex,
                    newBuilding.size
                )
            )
        } else {
            updatedSettlementMaps.add(oldTownMap.removeBuilding(oldBuilding.id))
        }
    } else if (newBuilding.position is InSettlementMap) {
        val newTownMap = state.getSettlementMapStorage().getOrThrow(newBuilding.position.map)

        updatedSettlementMaps.add(newTownMap.updateBuilding(newBuilding.id, newBuilding.position.tileIndex, newBuilding.size))
    }

    return noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().update(newBuilding),
                state.getSettlementMapStorage().update(updatedSettlementMaps),
            )
        )
    )
}

val UPDATE_BUILDING_LOT: Reducer<UpdateActionLot, State> = { state, action ->
    val oldBuilding = state.getBuildingStorage().getOrThrow(action.id)

    if (oldBuilding.position is InSettlementMap) {
        val oldTownMap = state.getSettlementMapStorage().getOrThrow(oldBuilding.position.map)
        val building = action.applyTo(oldBuilding)

        val townMap = oldTownMap.updateBuilding(action.id, action.tileIndex, action.size)

        noFollowUps(
            state.updateStorage(
                listOf(
                    state.getBuildingStorage().update(building),
                    state.getSettlementMapStorage().update(townMap),
                )
            )
        )
    } else {
        error("Updating the building lot requires InTownMap!")
    }
}

fun checkArchitecturalStyle(state: State, building: Building) {
    if (building.style != null) {
        val style = state.getArchitecturalStyleStorage().getOrThrow(building.style)

        validateStartDate(state, style, building.id, building.constructionDate)
    }
}

fun checkAddress(
    state: State,
    building: BuildingId,
    position: Position,
    address: Address,
) {
    when (address) {
        is CrossingAddress -> {
            require(address.streets.toSet().size == address.streets.size) { "List of streets contains duplicates!" }
            state.getStreetStorage().require(address.streets)

            if (position is InSettlementMap) {
                address.streets.forEach { street ->
                    checkIfStreetIsPartOfTown(state, position.map, street)
                }
            }
        }

        NoAddress -> doNothing()
        is StreetAddress -> {
            state.getStreetStorage().require(address.street)

            val buildings = state.getBuildingsForStreet(address.street)
                .filter { it.id != building }
            require(!getUsedHouseNumbers(buildings, address.street).contains(address.houseNumber)) {
                "House number ${address.houseNumber} already used for ${address.street.print()}!"
            }

            if (position is InSettlementMap) {
                checkIfStreetIsPartOfTown(state, position.map, address.street)
            }
        }

        is SettlementAddress -> {
            val buildings = state.getBuildingsForPosition(position)
                .filter { it.id != building }
            require(!getUsedHouseNumbers(buildings).contains(address.houseNumber)) {
                "House number ${address.houseNumber} already used for ${position.getId()?.print()}!"
            }
        }
    }
}

private fun checkIfStreetIsPartOfTown(
    state: State,
    settlementMapId: SettlementMapId,
    streetId: StreetId,
) {
    require(state.getStreetIds(settlementMapId).contains(streetId)) {
        "Street ${streetId.value} is not part of ${settlementMapId.print()}!"
    }
}

fun validateBuildingPurpose(
    state: State,
    building: Building,
) {
    when (building.purpose) {
        is ApartmentHouse -> {
            val min = state.getMinNumberOfApartment(building.id)
            require(building.purpose.apartments >= min) {
                "The apartment house ${building.id.value} requires at least $min apartments!"
            }
        }

        is SingleBusiness -> doNothing()
        is SingleFamilyHouse -> doNothing()
        is BusinessAndHome -> doNothing()
        UndefinedBuildingPurpose -> doNothing()
    }

    if (!building.purpose.getType().isHome()) {
        require(state.getCharactersLivingIn(building.id).isEmpty()) {
            "Cannot change the purpose, while characters are living in it!"
        }
    }
}