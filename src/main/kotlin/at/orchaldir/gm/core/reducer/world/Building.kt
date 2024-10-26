package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.action.UpdateBuildingLot
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.getCharactersLivingIn
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.world.exists
import at.orchaldir.gm.core.selector.world.getMinNumberOfApartment
import at.orchaldir.gm.core.selector.world.getStreetIds
import at.orchaldir.gm.core.selector.world.getUsedHouseNumbers
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val ADD_BUILDING: Reducer<AddBuilding, State> = { state, action ->
    val buildingId = state.getBuildingStorage().nextId
    val oldTown = state.getTownStorage().getOrThrow(action.town)
    val town = oldTown.build(action.tileIndex, action.size, BuildingTile(buildingId))
    val lot = BuildingLot(action.town, action.tileIndex, action.size)
    val building = Building(buildingId, lot = lot, constructionDate = state.time.currentDate)

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().add(building),
                state.getTownStorage().update(town),
            )
        )
    )
}

val DELETE_BUILDING: Reducer<DeleteBuilding, State> = { state, action ->
    val id = action.id
    val building = state.getBuildingStorage().getOrThrow(id)
    val oldTown = state.getTownStorage().getOrThrow(building.lot.town)
    val town = oldTown.removeBuilding(building.id)

    require(
        state.getCharactersLivingIn(id).isEmpty()
    ) { "Cannot delete building ${id.value}, because it has inhabitants!" }

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().remove(id),
                state.getTownStorage().update(town),
            )
        )
    )
}

val UPDATE_BUILDING: Reducer<UpdateBuilding, State> = { state, action ->
    val oldBuilding = state.getBuildingStorage().getOrThrow(action.id)

    checkAddress(state, oldBuilding.lot.town, oldBuilding.address, action.address)
    checkArchitecturalStyle(state, action)
    checkOwnership(state, action.ownership, action.constructionDate)
    checkPurpose(state, oldBuilding, action)

    val building = action.applyTo(oldBuilding)

    noFollowUps(state.updateStorage(state.getBuildingStorage().update(building)))
}

val UPDATE_BUILDING_LOT: Reducer<UpdateBuildingLot, State> = { state, action ->
    val oldBuilding = state.getBuildingStorage().getOrThrow(action.id)
    val oldTown = state.getTownStorage().getOrThrow(oldBuilding.lot.town)
    val building = action.applyTo(oldBuilding)

    val town = oldTown.removeBuilding(action.id)
        .build(action.tileIndex, action.size, BuildingTile(oldBuilding.id))

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().update(building),
                state.getTownStorage().update(town),
            )
        )
    )
}

private fun checkArchitecturalStyle(state: State, action: UpdateBuilding) {
    val style = state.getArchitecturalStyleStorage().getOrThrow(action.style)

    checkStartDate(state, style, action.id, action.constructionDate)
}

private fun checkAddress(
    state: State,
    townId: TownId,
    oldAddress: Address,
    address: Address,
) {
    when (address) {
        is CrossingAddress -> {
            require(address.streets.toSet().size == address.streets.size) { "List of streets contains duplicates!" }

            address.streets.forEach { street ->
                state.getStreetStorage().require(street)
                checkIfStreetIsPartOfTown(state, townId, street)
            }
        }

        NoAddress -> doNothing()
        is StreetAddress -> {
            state.getStreetStorage().require(address.street)

            if (!(oldAddress is StreetAddress && oldAddress.houseNumber == address.houseNumber)) {
                require(!state.getUsedHouseNumbers(townId, address.street).contains(address.houseNumber)) {
                    "House number ${address.houseNumber} already used for street ${address.street.value}!"
                }
            }

            checkIfStreetIsPartOfTown(state, townId, address.street)
        }

        is TownAddress -> {
            if (!(oldAddress is TownAddress && oldAddress.houseNumber == address.houseNumber)) {
                require(!state.getUsedHouseNumbers(townId).contains(address.houseNumber)) {
                    "House number ${address.houseNumber} already used for the town!"
                }
            }
        }
    }
}

private fun checkIfStreetIsPartOfTown(
    state: State,
    townId: TownId,
    streetId: StreetId,
) {
    require(state.getStreetIds(townId).contains(streetId)) {
        "Street ${streetId.value} is not part of town ${townId.value}!"
    }
}

private fun checkOwnership(
    state: State,
    ownership: Ownership,
    creationDate: Date,
) {
    checkOwner(state, ownership.owner, "owner")

    val calendar = state.getDefaultCalendar()
    var min = creationDate


    ownership.previousOwners.withIndex().forEach { (index, previous) ->
        checkOwner(state, previous.owner, "previous owner")
        checkOwnerStart(state, previous.owner, "${index + 1}.previous owner", min)
        require(calendar.compareTo(previous.until, min) > 0) { "${index + 1}.previous owner's until is too early!" }

        min = previous.until
    }

    checkOwnerStart(state, ownership.owner, "Owner", min)
}

private fun checkOwner(
    state: State,
    owner: Owner,
    noun: String,
) {
    when (owner) {
        is OwnedByCharacter -> state.getCharacterStorage()
            .require(owner.character) { "Cannot use an unknown character ${owner.character.value} as $noun!" }

        is OwnedByTown -> state.getTownStorage()
            .require(owner.town) { "Cannot use an unknown town ${owner.town.value} as $noun!" }

        else -> doNothing()
    }
}

private fun checkOwnerStart(
    state: State,
    owner: Owner,
    noun: String,
    startInterval: Date,
) {
    val exists = when (owner) {
        is OwnedByCharacter -> state.isAlive(owner.character, startInterval)
        is OwnedByTown -> state.exists(owner.town, startInterval)
        else -> return
    }

    require(exists) { "$noun didn't exist at the start of their ownership!" }
}

private fun checkPurpose(
    state: State,
    oldBuilding: Building,
    action: UpdateBuilding,
) {
    if (oldBuilding.purpose.getType() != action.purpose.getType()) {
        require(state.getCharactersLivingIn(oldBuilding.id).isEmpty()) {
            "Cannot change the purpose, while characters are living in it!"
        }
    }
    when (action.purpose) {
        is ApartmentHouse -> {
            val min = state.getMinNumberOfApartment(action.id)
            require(action.purpose.apartments >= min) {
                "The apartment house ${action.id.value} requires at least $min apartments!"
            }
        }
        SingleFamilyHouse -> doNothing()
    }
}