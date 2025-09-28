package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.LoadData
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val LOAD_DATA: Reducer<LoadData, State> = { _, action ->
    val newState = State.load(action.path)

    newState.validate()

    noFollowUps(newState)
}