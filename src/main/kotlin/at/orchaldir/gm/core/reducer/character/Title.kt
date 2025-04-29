package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateTitle
import at.orchaldir.gm.core.action.DeleteTitle
import at.orchaldir.gm.core.action.UpdateTitle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.selector.character.canDeleteTitle
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TITLE: Reducer<CreateTitle, State> = { state, _ ->
    val title = Title(state.getTitleStorage().nextId)

    noFollowUps(state.updateStorage(state.getTitleStorage().add(title)))
}

val DELETE_TITLE: Reducer<DeleteTitle, State> = { state, action ->
    state.getTitleStorage().require(action.id)
    require(state.canDeleteTitle(action.id)) { "Title ${action.id.value} is used by characters" }

    noFollowUps(state.updateStorage(state.getTitleStorage().remove(action.id)))
}

val UPDATE_TITLE: Reducer<UpdateTitle, State> = { state, action ->
    val title = action.title
    state.getTitleStorage().require(title.id)


    noFollowUps(state.updateStorage(state.getTitleStorage().update(title)))
}
