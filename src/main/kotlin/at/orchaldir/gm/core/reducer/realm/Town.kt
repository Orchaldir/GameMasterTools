package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkHistory
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.character.countCurrentOrFormerEmployees
import at.orchaldir.gm.core.selector.realm.getRealmsWithCapital
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousCapital
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.core.selector.world.getTownMaps
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TOWN: Reducer<CreateTown, State> = { state, _ ->
    val town = Town(state.getTownStorage().nextId, foundingDate = state.getCurrentDate())

    noFollowUps(state.updateStorage(state.getTownStorage().add(town)))
}

val DELETE_TOWN: Reducer<DeleteTown, State> = { state, action ->
    state.getTownStorage().require(action.id)

    validateCanDelete(state.countCurrentOrFormerEmployees(action.id) == 0, action.id, "it has or had employees")

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)
    validateCanDelete(state.getTownMaps(action.id).isEmpty(), action.id, "it has a town map")
    validateCanDelete(state.getRealmsWithCapital(action.id).isEmpty(), action.id, "it is a capital")
    validateCanDelete(state.getRealmsWithPreviousCapital(action.id).isEmpty(), action.id, "it was a capital")

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
    state.getDataSourceStorage().require(town.sources)
    checkHistory(state, town.owner, town.foundingDate, "owner") { _, realmId, _, date ->
        if (realmId != null) {
            state.requireExists(state.getRealmStorage(), realmId, date)
        }
    }
}
