package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.CreateGod
import at.orchaldir.gm.core.action.DeleteGod
import at.orchaldir.gm.core.action.UpdateGod
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.selector.religion.canDeleteGod
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_GOD: Reducer<CreateGod, State> = { state, _ ->
    val god = God(state.getGodStorage().nextId)

    noFollowUps(state.updateStorage(state.getGodStorage().add(god)))
}

val DELETE_GOD: Reducer<DeleteGod, State> = { state, action ->
    state.getGodStorage().require(action.id)
    require(state.canDeleteGod(action.id)) { "The god ${action.id.value} is used!" }

    noFollowUps(state.updateStorage(state.getGodStorage().remove(action.id)))
}

val UPDATE_GOD: Reducer<UpdateGod, State> = { state, action ->
    val god = action.god
    state.getGodStorage().require(god.id)
    god.domains.forEach { state.getDomainStorage().require(it) }
    god.personality.forEach { state.getPersonalityTraitStorage().require(it) }

    noFollowUps(state.updateStorage(state.getGodStorage().update(god)))
}
