package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.reducer.magic.validateSpell
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.noFollowUps

fun reduceEditElement(
    state: State,
    element: Element<*>,
): Pair<State, List<Action>> = when (element) {
    is Spell -> editElement(state, element) {
        validateSpell(state, it)
    }
    else -> error("Creating is not supported!")
}

fun <ID : Id<ID>, ELEMENT : Element<ID>, Action> editElement(
    state: State,
    element: ELEMENT,
    validate: (ELEMENT) -> Unit
): Pair<State, List<Action>> {
    val storage = state.getStorage<ID, ELEMENT>(element.id())
    storage.require(element.id())

    validate(element)

    return noFollowUps(state.updateStorage(storage.update(element)))
}

