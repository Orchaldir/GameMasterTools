package at.orchaldir.gm.core.reducer.source

import at.orchaldir.gm.core.action.CreateDataSource
import at.orchaldir.gm.core.action.DeleteDataSource
import at.orchaldir.gm.core.action.UpdateDataSource
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.source.DataSource
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.util.canDeleteDataSource
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_DATA_SOURCE: Reducer<CreateDataSource, State> = { state, _ ->
    val source = DataSource(state.getDataSourceStorage().nextId)

    noFollowUps(state.updateStorage(state.getDataSourceStorage().add(source)))
}

val DELETE_DATA_SOURCE: Reducer<DeleteDataSource, State> = { state, action ->
    state.getDataSourceStorage().require(action.id)
    validateCanDelete(state.canDeleteDataSource(action.id), action.id)

    noFollowUps(state.updateStorage(state.getDataSourceStorage().remove(action.id)))
}

val UPDATE_DATA_SOURCE: Reducer<UpdateDataSource, State> = { state, action ->
    state.getDataSourceStorage().require(action.source.id)

    noFollowUps(state.updateStorage(state.getDataSourceStorage().update(action.source)))
}
