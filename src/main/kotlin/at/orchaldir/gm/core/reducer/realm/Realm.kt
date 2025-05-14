package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateRealm
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.realm.canDeleteRealm
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_REALM: Reducer<CreateRealm, State> = { state, _ ->
    val realm = Realm(state.getRealmStorage().nextId)

    noFollowUps(state.updateStorage(state.getRealmStorage().add(realm)))
}

val DELETE_REALM: Reducer<DeleteRealm, State> = { state, action ->
    state.getRealmStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id)
    validateCanDelete(state.canDeleteRealm(action.id), action.id)

    noFollowUps(state.updateStorage(state.getRealmStorage().remove(action.id)))
}

val UPDATE_REALM: Reducer<UpdateRealm, State> = { state, action ->
    val realm = action.realm
    state.getRealmStorage().require(realm.id)

    validateRealm(state, realm)

    noFollowUps(state.updateStorage(state.getRealmStorage().update(realm)))
}

fun validateRealm(state: State, realm: Realm) {
    validateCreator(state, realm.founder, realm.id, realm.date, "founder")
}
