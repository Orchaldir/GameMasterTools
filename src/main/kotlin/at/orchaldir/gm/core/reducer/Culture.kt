package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CULTURE: Reducer<CreateCulture, State> = { state, _ ->
    val culture = Culture(state.cultures.nextId)

    noFollowUps(state.copy(cultures = state.cultures.add(culture)))
}

val DELETE_CULTURE: Reducer<DeleteCulture, State> = { state, action ->
    state.cultures.require(action.id)
    require(state.canDelete(action.id)) { "Culture ${action.id.value} is used by characters" }

    noFollowUps(state.copy(cultures = state.cultures.remove(action.id)))
}

val UPDATE_CULTURE: Reducer<UpdateCulture, State> = { state, action ->
    state.cultures.require(action.culture.id)
    action.culture.namingConvention.getNameLists()
        .forEach { state.nameLists.require(it) }

    noFollowUps(state.copy(cultures = state.cultures.update(action.culture)))
}