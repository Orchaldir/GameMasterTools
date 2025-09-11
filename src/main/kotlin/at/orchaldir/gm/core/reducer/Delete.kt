package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.noFollowUps

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