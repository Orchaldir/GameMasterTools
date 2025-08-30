package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.action.UpdateBuildingLot
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOwnership
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.world.getMinNumberOfApartment
import at.orchaldir.gm.core.selector.world.getStreetIds
import at.orchaldir.gm.core.selector.world.getUsedHouseNumbers
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_BUILDING: Reducer<AddBuilding, State> = { state, action ->
    val buildingId = state.getBuildingStorage().nextId
    val oldTownMap = state.getTownMapStorage().getOrThrow(action.town)
    val townMap = oldTownMap.build(action.tileIndex, action.size, BuildingTile(buildingId))
    val position = InTownMap(action.town, action.tileIndex)
    val building =
        Building(buildingId, position = position, size = action.size, constructionDate = state.getCurrentDate())

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().add(building),
                state.getTownMapStorage().update(townMap),
            )
        )
    )
}

val DELETE_BUILDING: Reducer<DeleteBuilding, State> = { state, action ->
    val id = action.id

    validateCanDelete(state.getCharactersLivingIn(id).isEmpty(), id, "it has inhabitants")
    validateCanDelete(state.getCharactersPreviouslyLivingIn(id).isEmpty(), id, "it had inhabitants")

    val building = state.getBuildingStorage().getOrThrow(id)

    if (building.position is InTownMap) {
        val oldTownMap = state.getTownMapStorage().getOrThrow(building.position.townMap)
        val townMap = oldTownMap.removeBuilding(building.id)

        noFollowUps(
            state.updateStorage(
                listOf(
                    state.getBuildingStorage().remove(id),
                    state.getTownMapStorage().update(townMap),
                )
            )
        )
    } else {
        noFollowUps(state.updateStorage(state.getBuildingStorage().remove(id)))
    }
}

val UPDATE_BUILDING: Reducer<UpdateBuilding, State> = { state, action ->
    //checkAddress(state, oldBuilding.lot.town, oldBuilding.address, action.address)
    validateBuilding(state, action.building)

    noFollowUps(state.updateStorage(state.getBuildingStorage().update(action.building)))
}

fun validateBuilding(
    state: State,
    building: Building,
) {
    checkDate(state, building.constructionDate, "Building")
    checkArchitecturalStyle(state, building)
    validateCreator(state, building.builder, building.id, building.constructionDate, "Builder")
    checkOwnership(state, building.ownership, building.constructionDate)
    checkPurpose(state, building)
}

val UPDATE_BUILDING_LOT: Reducer<UpdateBuildingLot, State> = { state, action ->
    val oldBuilding = state.getBuildingStorage().getOrThrow(action.id)

    if (oldBuilding.position is InTownMap) {
        val oldTownMap = state.getTownMapStorage().getOrThrow(oldBuilding.position.townMap)
        val building = action.applyTo(oldBuilding)

        val townMap = oldTownMap.updateBuilding(action.id, action.tileIndex, action.size)

        noFollowUps(
            state.updateStorage(
                listOf(
                    state.getBuildingStorage().update(building),
                    state.getTownMapStorage().update(townMap),
                )
            )
        )
    } else {
        error("Updating the building lot requires InTownMap!")
    }
}

private fun checkArchitecturalStyle(state: State, building: Building) {
    if (building.style != null) {
        val style = state.getArchitecturalStyleStorage().getOrThrow(building.style)

        checkStartDate(state, style, building.id, building.constructionDate)
    }
}

private fun checkAddress(
    state: State,
    townMapId: TownMapId,
    oldAddress: Address,
    address: Address,
) {
    when (address) {
        is CrossingAddress -> {
            require(address.streets.toSet().size == address.streets.size) { "List of streets contains duplicates!" }

            address.streets.forEach { street ->
                state.getStreetStorage().require(street)
                checkIfStreetIsPartOfTown(state, townMapId, street)
            }
        }

        NoAddress -> doNothing()
        is StreetAddress -> {
            state.getStreetStorage().require(address.street)

            if (!(oldAddress is StreetAddress && oldAddress.houseNumber == address.houseNumber)) {
                require(!state.getUsedHouseNumbers(townMapId, address.street).contains(address.houseNumber)) {
                    "House number ${address.houseNumber} already used for street ${address.street.value}!"
                }
            }

            checkIfStreetIsPartOfTown(state, townMapId, address.street)
        }

        is TownAddress -> {
            if (!(oldAddress is TownAddress && oldAddress.houseNumber == address.houseNumber)) {
                require(!state.getUsedHouseNumbers(townMapId).contains(address.houseNumber)) {
                    "House number ${address.houseNumber} already used for the town!"
                }
            }
        }
    }
}

private fun checkIfStreetIsPartOfTown(
    state: State,
    townMapId: TownMapId,
    streetId: StreetId,
) {
    require(state.getStreetIds(townMapId).contains(streetId)) {
        "Street ${streetId.value} is not part of town ${townMapId.value}!"
    }
}

private fun checkPurpose(
    state: State,
    action: Building,
) {
    when (action.purpose) {
        is ApartmentHouse -> {
            val min = state.getMinNumberOfApartment(action.id)
            require(action.purpose.apartments >= min) {
                "The apartment house ${action.id.value} requires at least $min apartments!"
            }
        }

        is SingleBusiness -> doNothing()
        is SingleFamilyHouse -> doNothing()
        is BusinessAndHome -> doNothing()
    }

    if (!action.purpose.getType().isHome()) {
        require(state.getCharactersLivingIn(action.id).isEmpty()) {
            "Cannot change the purpose, while characters are living in it!"
        }
    }
}