package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.UpdateMagicTradition
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_MAGIC_TRADITION: Reducer<UpdateMagicTradition, State> = { state, action ->
    val tradition = action.tradition
    state.getMagicTraditionStorage().require(tradition.id)

    validateMagicTradition(state, tradition)

    noFollowUps(state.updateStorage(state.getMagicTraditionStorage().update(tradition)))
}

fun validateMagicTradition(state: State, tradition: MagicTradition) {
    state.getSpellGroupStorage().require(tradition.groups)
    state.getDataSourceStorage().require(tradition.sources)
}
