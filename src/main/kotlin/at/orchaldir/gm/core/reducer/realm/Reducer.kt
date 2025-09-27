package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.realm.*
import at.orchaldir.gm.utils.redux.Reducer

val REALM_REDUCER: Reducer<RealmAction, State> = { state, action ->
    when (action) {
        // battle
        is DeleteBattle -> deleteElement(state, action.id, State::canDeleteBattle)
        // catastrophe
        is DeleteCatastrophe -> deleteElement(state, action.id, State::canDeleteCatastrophe)
        // district
        is DeleteDistrict -> deleteElement(state, action.id, State::canDeleteDistrict)
        // legal code
        is DeleteLegalCode -> deleteElement(state, action.id, State::canDeleteLegalCode)
        // realm
        is DeleteRealm -> deleteElement(state, action.id, State::canDeleteRealm)
        // town
        is DeleteTown -> deleteElement(state, action.id, State::canDeleteTown)
        // treaty
        is DeleteTreaty -> deleteElement(state, action.id, State::canDeleteTreaty)
        // war
        is DeleteWar -> deleteElement(state, action.id, State::canDeleteWar)
    }
}
