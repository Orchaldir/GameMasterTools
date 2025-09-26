package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateTitle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_TITLE: Reducer<UpdateTitle, State> = { state, action ->
    val title = action.title
    state.getTitleStorage().require(title.id)


    noFollowUps(state.updateStorage(state.getTitleStorage().update(title)))
}
