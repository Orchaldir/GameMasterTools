package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.DeleteElement
import at.orchaldir.gm.core.action.LoadData
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.world.canDeleteRiver
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val LOAD_DATA: Reducer<LoadData, State> = { _, action ->
    val newState = State.load(action.path)

    newState.validate()

    noFollowUps(newState)
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> createDeleteElement(): Reducer<DeleteElement<ID>, State> {
    return { state, action ->
        val storage = state.getStorage<ID, ELEMENT>(action.id)

        storage.require(action.id)
        action.validation.invoke(state, action.id).validate()

        noFollowUps(state.updateStorage(storage.remove(action.id)))
    }
}

