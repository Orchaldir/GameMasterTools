package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateLegalCode
import at.orchaldir.gm.core.action.DeleteLegalCode
import at.orchaldir.gm.core.action.UpdateLegalCode
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.realm.canDeleteLegalCode
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_LEGAL_CODE: Reducer<CreateLegalCode, State> = { state, _ ->
    val code = LegalCode(state.getLegalCodeStorage().nextId)

    noFollowUps(state.updateStorage(state.getLegalCodeStorage().add(code)))
}

val UPDATE_LEGAL_CODE: Reducer<UpdateLegalCode, State> = { state, action ->
    val code = action.code
    state.getLegalCodeStorage().require(code.id)

    validateLegalCode(state, code)

    noFollowUps(state.updateStorage(state.getLegalCodeStorage().update(code)))
}

fun validateLegalCode(state: State, code: LegalCode) {
    validateCreator(state, code.creator, code.id, code.date, "creator")
}
