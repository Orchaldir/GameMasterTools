package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.realm.canDeleteBattle
import at.orchaldir.gm.core.selector.realm.canDeleteCatastrophe
import at.orchaldir.gm.core.selector.realm.canDeleteDistrict
import at.orchaldir.gm.core.selector.realm.canDeleteLegalCode
import at.orchaldir.gm.core.selector.realm.canDeleteRealm
import at.orchaldir.gm.core.selector.realm.canDeleteTown
import at.orchaldir.gm.core.selector.religion.canDeleteDomain
import at.orchaldir.gm.utils.redux.Reducer

val REALM_REDUCER: Reducer<RealmAction, State> = { state, action ->
    when (action) {
        // battle
        is CreateBattle -> CREATE_BATTLE(state, action)
        is DeleteBattle -> deleteElement(state, action.id, State::canDeleteBattle)
        is UpdateBattle -> UPDATE_BATTLE(state, action)
        // catastrophe
        is CreateCatastrophe -> CREATE_CATASTROPHE(state, action)
        is DeleteCatastrophe -> deleteElement(state, action.id, State::canDeleteCatastrophe)
        is UpdateCatastrophe -> UPDATE_CATASTROPHE(state, action)
        // district
        is CreateDistrict -> CREATE_DISTRICT(state, action)
        is DeleteDistrict -> deleteElement(state, action.id, State::canDeleteDistrict)
        is UpdateDistrict -> UPDATE_DISTRICT(state, action)
        // legal code
        is CreateLegalCode -> CREATE_LEGAL_CODE(state, action)
        is DeleteLegalCode -> deleteElement(state, action.id, State::canDeleteLegalCode)
        is UpdateLegalCode -> UPDATE_LEGAL_CODE(state, action)
        // realm
        is CreateRealm -> CREATE_REALM(state, action)
        is DeleteRealm -> deleteElement(state, action.id, State::canDeleteRealm)
        is UpdateRealm -> UPDATE_REALM(state, action)
        // town
        is CreateTown -> CREATE_TOWN(state, action)
        is DeleteTown -> deleteElement(state, action.id, State::canDeleteTown)
        is UpdateTown -> UPDATE_TOWN(state, action)
        // treaty
        is CreateTreaty -> CREATE_TREATY(state, action)
        is DeleteTreaty -> DELETE_TREATY(state, action)
        is UpdateTreaty -> UPDATE_TREATY(state, action)
        // war
        is CreateWar -> CREATE_WAR(state, action)
        is DeleteWar -> DELETE_WAR(state, action)
        is UpdateWar -> UPDATE_WAR(state, action)
    }
}
