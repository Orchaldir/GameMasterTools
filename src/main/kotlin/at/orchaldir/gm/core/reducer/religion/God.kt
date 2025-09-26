package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.UpdateGod
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.reducer.util.checkAuthenticity
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_GOD: Reducer<UpdateGod, State> = { state, action ->
    val god = action.god
    state.getGodStorage().require(god.id)

    validateGod(state, god)

    noFollowUps(state.updateStorage(state.getGodStorage().update(god)))
}

fun validateGod(state: State, god: God) {
    state.getDomainStorage().require(god.domains)
    state.getPersonalityTraitStorage().require(god.personality)
    checkAuthenticity(state, god.authenticity)
    state.getDataSourceStorage().require(god.sources)
}
