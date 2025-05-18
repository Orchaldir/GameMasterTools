package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateTreaty
import at.orchaldir.gm.core.action.DeleteTreaty
import at.orchaldir.gm.core.action.UpdateTreaty
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.realm.canDeleteTreaty
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TREATY: Reducer<CreateTreaty, State> = { state, _ ->
    val treaty = Treaty(state.getTreatyStorage().nextId)

    noFollowUps(state.updateStorage(state.getTreatyStorage().add(treaty)))
}

val DELETE_TREATY: Reducer<DeleteTreaty, State> = { state, action ->
    state.getTreatyStorage().require(action.id)

    validateCanDelete(state.canDeleteTreaty(action.id), action.id, "it is used")

    noFollowUps(state.updateStorage(state.getTreatyStorage().remove(action.id)))
}

val UPDATE_TREATY: Reducer<UpdateTreaty, State> = { state, action ->
    val treaty = action.treaty
    state.getTreatyStorage().require(treaty.id)

    validateTreaty(state, treaty)

    noFollowUps(state.updateStorage(state.getTreatyStorage().update(treaty)))
}

fun validateTreaty(state: State, treaty: Treaty) {
    checkDate(state, treaty.date, "Treaty")
    state.getDataSourceStorage().require(treaty.sources)
}
