package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateNameList
import at.orchaldir.gm.core.action.DeleteNameList
import at.orchaldir.gm.core.action.UpdateNameList
import at.orchaldir.gm.core.model.NameList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps
import at.orchaldir.gm.utils.titlecaseFirstChar

val CREATE_NAME_LIST: Reducer<CreateNameList, State> = { state, _ ->
    val nameList = NameList(state.nameLists.nextId)

    noFollowUps(state.copy(nameLists = state.nameLists.add(nameList)))
}

val DELETE_NAME_LIST: Reducer<DeleteNameList, State> = { state, action ->
    state.nameLists.require(action.id)
    require(state.canDelete(action.id)) { "Name list ${action.id.value} is used" }

    noFollowUps(state.copy(nameLists = state.nameLists.remove(action.id)))
}

val UPDATE_NAME_LIST: Reducer<UpdateNameList, State> = { state, action ->
    val nameList = action.nameList

    state.nameLists.require(nameList.id)

    val cleaned = nameList.copy(names = nameList.names
        .asSequence()
        .flatMap { it.split(",", ".", ";") }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.titlecaseFirstChar() }
        .sorted()
        .toList())

    noFollowUps(state.copy(nameLists = state.nameLists.update(cleaned)))
}
