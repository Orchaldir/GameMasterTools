package at.orchaldir.gm.core.reducer.util.source

import at.orchaldir.gm.core.action.UpdateDataSource
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_DATA_SOURCE: Reducer<UpdateDataSource, State> = { state, action ->
    state.getDataSourceStorage().require(action.source.id)

    noFollowUps(state.updateStorage(state.getDataSourceStorage().update(action.source)))
}
