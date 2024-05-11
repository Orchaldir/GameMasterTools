package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.DeleteCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    noFollowUps(state.copy(characters = state.characters.remove(action.id)))
}