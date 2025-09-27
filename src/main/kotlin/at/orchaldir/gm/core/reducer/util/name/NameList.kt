package at.orchaldir.gm.core.reducer.util.name

import at.orchaldir.gm.core.action.Action
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.utils.redux.noFollowUps

fun updateNameList(state: State, nameList: NameList): Pair<State, List<Action>> {
    state.getNameListStorage().require(nameList.id)

    val cleaned = nameList.copy(
        names = nameList.names
            .sortedBy { it.text }
            .toList())

    return noFollowUps(state.updateStorage(state.getNameListStorage().update(cleaned)))
}
