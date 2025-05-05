package at.orchaldir.gm.core.reducer.world.town

import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.character.countCurrentOrFormerEmployees
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN: Reducer<CreateTown, State> = { state, _ ->
    val town = Town(state.getTownStorage().nextId, foundingDate = state.getCurrentDate())

    noFollowUps(state.updateStorage(state.getTownStorage().add(town)))
}

val DELETE_TOWN: Reducer<DeleteTown, State> = { state, action ->
    state.getTownStorage().require(action.id)

    require(state.countCurrentOrFormerEmployees(action.id) == 0) {
        "Cannot delete Town ${action.id.value}, because it has or had employees!"
    }

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)

    noFollowUps(state.updateStorage(state.getTownStorage().remove(action.id)))
}

val UPDATE_TOWN: Reducer<UpdateTown, State> = { state, action ->
    val town = action.town
    state.getTownStorage().require(town.id)

    validateTown(state, town)

    noFollowUps(state.updateStorage(state.getTownStorage().update(town)))
}

fun validateTown(state: State, town: Town) {
    checkDate(state, town.foundingDate, "Town")
    validateCreator(state, town.founder, town.id, town.foundingDate, "founder")
}
