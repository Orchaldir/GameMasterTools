package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.character.*
import at.orchaldir.gm.utils.redux.Reducer

val REDUCER: Reducer<Action, State> = { state, action ->
    when (action) {
        // character actions
        is CreateCharacter -> CREATE_CHARACTER(state, action)
        is DeleteCharacter -> DELETE_CHARACTER(state, action)
        is UpdateCharacter -> UPDATE_CHARACTER(state, action)
        is UpdateAppearance -> UPDATE_APPEARANCE(state, action)
        is UpdateRelationships -> UPDATE_RELATIONSHIPS(state, action)
        // character's languages actions
        is AddLanguage -> ADD_LANGUAGE(state, action)
        is RemoveLanguages -> REMOVE_LANGUAGES(state, action)
        // character's relationship actions
        // culture actions
        is CreateCulture -> CREATE_CULTURE(state, action)
        is DeleteCulture -> DELETE_CULTURE(state, action)
        is UpdateCulture -> UPDATE_CULTURE(state, action)
        // language actions
        is CreateItemTemplate -> CREATE_ITEM_TEMPLATE(state, action)
        is DeleteItemTemplate -> DELETE_ITEM_TEMPLATE(state, action)
        is UpdateItemTemplate -> UPDATE_ITEM_TEMPLATE(state, action)
        // language actions
        is CreateLanguage -> CREATE_LANGUAGE(state, action)
        is DeleteLanguage -> DELETE_LANGUAGE(state, action)
        is UpdateLanguage -> UPDATE_LANGUAGE(state, action)
        // language actions
        is CreateNameList -> CREATE_NAME_LIST(state, action)
        is DeleteNameList -> DELETE_NAME_LIST(state, action)
        is UpdateNameList -> UPDATE_NAME_LIST(state, action)
        // personality actions
        is CreatePersonalityTrait -> CREATE_PERSONALITY_TRAIT(state, action)
        is DeletePersonalityTrait -> DELETE_PERSONALITY_TRAIT(state, action)
        is UpdatePersonalityTrait -> UPDATE_PERSONALITY_TRAIT(state, action)
        // race actions
        is CreateRace -> CREATE_RACE(state, action)
        is DeleteRace -> DELETE_RACE(state, action)
        is UpdateRace -> UPDATE_RACE(state, action)
    }
}
