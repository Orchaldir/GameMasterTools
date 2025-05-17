package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateRealm
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.reducer.util.checkHistory
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.selector.realm.canDeleteRealm
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_REALM: Reducer<CreateRealm, State> = { state, _ ->
    val realm = Realm(state.getRealmStorage().nextId)

    noFollowUps(state.updateStorage(state.getRealmStorage().add(realm)))
}

val DELETE_REALM: Reducer<DeleteRealm, State> = { state, action ->
    state.getRealmStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)
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
    checkHistory(state, realm.capital, realm.date, "capital") { _, townId, _, date ->
        if (townId != null) {
            state.requireExists(state.getTownStorage(), townId, date)
        }
    }
    checkHistory(state, realm.currency, realm.date, "currency") { _, code, _, date ->
        if (code != null) {
            state.requireExists(state.getCurrencyStorage(), code, date)
        }
    }
    checkHistory(state, realm.legalCode, realm.date, "legal code") { _, code, _, date ->
        if (code != null) {
            state.requireExists(state.getLegalCodeStorage(), code, date)
        }
    }
    checkHistory(state, realm.owner, realm.date, "owner") { _, realmId, _, date ->
        if (realmId != null) {
            state.requireExists(state.getRealmStorage(), realmId, date)
            require(realm.id != realmId) { "A realm cannot own itself!" }
        }
    }
    validateRealmStatus(state, realm.status, realm.date)
    validateHasStartAndEnd(state, realm)
}

fun validateRealmStatus(
    state: State,
    status: RealmStatus,
    startDate: Date?,
) = when (status) {
    LivingRealm -> doNothing()
    is DestroyedByCatastrophe -> state.requireExists(state.getCatastropheStorage(), status.catastrophe, startDate)
    is DestroyedByWar -> state.requireExists(state.getWarStorage(), status.war, startDate)
    is UndefinedEndOfRealm -> doNothing()
}
