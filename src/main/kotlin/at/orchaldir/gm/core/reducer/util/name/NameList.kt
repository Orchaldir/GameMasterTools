package at.orchaldir.gm.core.reducer.util.name

import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_NAME_LIST: Reducer<UpdateNameList, State> = { state, action ->
    val nameList = action.nameList

    state.getNameListStorage().require(nameList.id)

    val cleaned = nameList.copy(
        names = nameList.names
            .sortedBy { it.text }
            .toList())

    noFollowUps(state.updateStorage(state.getNameListStorage().update(cleaned)))
}
