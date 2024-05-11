package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateCharacter
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.characters.nextId)

    noFollowUps(state.copy(characters = state.characters.add(character)))
}