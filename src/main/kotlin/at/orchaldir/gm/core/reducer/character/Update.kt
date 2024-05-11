package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = Character(action.id, action.name, action.gender)

    noFollowUps(state.copy(characters = state.characters.update(character)))
}