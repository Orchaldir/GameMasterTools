package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Born
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.selector.getInventedLanguages
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CHARACTER: Reducer<CreateCharacter, State> = { state, _ ->
    val character = Character(state.characters.nextId)

    noFollowUps(state.copy(characters = state.characters.add(character)))
}

val DELETE_CHARACTER: Reducer<DeleteCharacter, State> = { state, action ->
    state.characters.require(action.id)

    val invented = state.getInventedLanguages(action.id)
    require(invented.isEmpty()) { "Cannot delete a character ${action.id.value}, because he is an language inventor" }

    noFollowUps(state.copy(characters = state.characters.remove(action.id)))
}

val UPDATE_CHARACTER: Reducer<UpdateCharacter, State> = { state, action ->
    val character = action.character

    state.characters.require(character.id)
    state.races.require(character.race)
    checkOrigin(state, character)
    character.personality.forEach { state.personalityTraits.require(it) }

    if (character.culture != null) {
        state.cultures.require(character.culture)
    }

    noFollowUps(state.copy(characters = state.characters.update(character)))
}

private fun checkOrigin(
    state: State,
    character: Character,
) {
    when (val origin = character.origin) {
        is Born -> {
            require(state.characters.contains(origin.mother)) { "Cannot use an unknown mother ${origin.mother.value}!" }
            require(state.characters.getOrThrow(origin.mother).gender == Gender.Female) { "Mother ${origin.mother.value} is not female!" }
            require(state.characters.contains(origin.father)) { "Cannot use an unknown father ${origin.father.value}!" }
            require(state.characters.getOrThrow(origin.father).gender == Gender.Male) { "Father ${origin.father.value} is not male!" }
        }

        else -> doNothing()
    }
}

val ADD_LANGUAGE: Reducer<AddLanguage, State> = { state, action ->
    state.languages.require(action.language)

    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(languages = character.languages + mapOf(action.language to action.level))

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}

val REMOVE_LANGUAGES: Reducer<RemoveLanguages, State> = { state, action ->
    action.languages.forEach { state.languages.require(it) }

    val character = state.characters.getOrThrow(action.id)
    val updated = character.copy(languages = character.languages - action.languages)

    noFollowUps(state.copy(characters = state.characters.update(updated)))
}
