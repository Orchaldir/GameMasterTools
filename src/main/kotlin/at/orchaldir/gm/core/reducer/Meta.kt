package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.LoadData
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val LOAD_DATA: Reducer<LoadData, State> = { _, action ->
    val newState = State.load(action.path)

    newState.validate()

    noFollowUps(newState)
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> createElement(
    state: State,
    element: ELEMENT,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(element.id())

    return noFollowUps(state.updateStorage(storage.add(element)))
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> cloneElement(
    state: State,
    id: ID,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(id)
    val original = storage.getOrThrow(id)
    val cloneId = storage.nextId
    val clone = original.clone(cloneId) as ELEMENT

    return noFollowUps(state.updateStorage(storage.add(clone)))
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> deleteElement(
    state: State,
    id: ID,
    validation: (State, ID) -> DeleteResult,
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(id)

    storage.require(id)
    validation.invoke(state, id).validate()

    return noFollowUps(state.updateStorage(storage.remove(id)))
}