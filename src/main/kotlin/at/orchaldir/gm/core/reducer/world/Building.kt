package at.orchaldir.gm.core.reducer.world

import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.core.selector.getDefaultCalendar
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
    val building = state.getBuildingStorage().getOrThrow(action.id)
    val oldTown = state.getTownStorage().getOrThrow(building.lot.town)
    val town = oldTown.removeBuilding(building.id)

    noFollowUps(
        state.updateStorage(
            listOf(
                state.getBuildingStorage().remove(action.id),
                state.getTownStorage().update(town),
            )
        )
    )
}

val UPDATE_BUILDING: Reducer<UpdateBuilding, State> = { state, action ->
    val oldBuilding = state.getBuildingStorage().getOrThrow(action.id)

    checkOwnership(state, action.ownership, action.constructionDate)

    val building = action.applyTo(oldBuilding)

    noFollowUps(state.updateStorage(state.getBuildingStorage().update(building)))
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
        checkOwnerStart(state, calendar, previous.owner, "${index + 1}.previous owner", min)
        require(calendar.compareTo(previous.until, min) > 0) { "${index + 1}.previous owner's until is too early!" }

        min = previous.until
    }

    checkOwnerStart(state, calendar, ownership.owner, "Owner", min)
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
    calendar: Calendar,
    owner: Owner,
    noun: String,
    startInterval: Date,
) {
    val startOwner = when (owner) {
        is OwnedByCharacter -> state.getCharacterStorage().getOrThrow(owner.character).birthDate
        is OwnedByTown -> state.getTownStorage().getOrThrow(owner.town).foundingDate
        else -> return
    }

    require(calendar.compareTo(startOwner, startInterval) <= 0) {
        "$noun didn't exist at the start of their ownership!"
    }
}