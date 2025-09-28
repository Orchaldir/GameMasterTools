package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.CHARACTER_REDUCER
import at.orchaldir.gm.core.reducer.world.WORLD_REDUCER
import at.orchaldir.gm.utils.redux.Reducer

val REDUCER: Reducer<Action, State> = { state, action ->
    when (action) {
        // meta
        is CreateAction<*> -> reduceCreateElement(state, action.id)
        is CloneAction<*> -> reduceCloneElement(state, action.id)
        is DeleteAction<*> -> reduceDeleteElement(state, action.id)
        is UpdateAction<*, *> -> reduceUpdateElement(state, action.element)
        is LoadData -> LOAD_DATA(state, action)
        // data
        is UpdateData -> UPDATE_DATA(state, action)
        // sub reducers
        is CharacterAction -> CHARACTER_REDUCER(state, action)
        is WorldAction -> WORLD_REDUCER(state, action)
    }
}
