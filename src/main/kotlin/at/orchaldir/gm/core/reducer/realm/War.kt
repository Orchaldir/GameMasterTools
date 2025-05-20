package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateWar
import at.orchaldir.gm.core.action.DeleteWar
import at.orchaldir.gm.core.action.UpdateWar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarStatus
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.selector.realm.canDeleteWar
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_WAR: Reducer<CreateWar, State> = { state, _ ->
    val war = War(state.getWarStorage().nextId)

    noFollowUps(state.updateStorage(state.getWarStorage().add(war)))
}

val DELETE_WAR: Reducer<DeleteWar, State> = { state, action ->
    state.getWarStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)
    validateCanDelete(state.canDeleteWar(action.id), action.id)

    noFollowUps(state.updateStorage(state.getWarStorage().remove(action.id)))
}

val UPDATE_WAR: Reducer<UpdateWar, State> = { state, action ->
    val war = action.war
    state.getWarStorage().require(war.id)

    validateWar(state, war)

    noFollowUps(state.updateStorage(state.getWarStorage().update(war)))
}

fun validateWar(state: State, war: War) {
    validateHasStartAndEnd(state, war)
    state.getRealmStorage().require(war.realms)
    validateWarStatus(state, war.status)
}

fun validateWarStatus(state: State, status: WarStatus) {
    state.getTreatyStorage().requireOptional(status.treaty())
}
