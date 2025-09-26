package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.character.canDeleteCharacter
import at.orchaldir.gm.core.selector.character.canDeleteCharacterTemplate
import at.orchaldir.gm.core.selector.character.canDeleteStatistic
import at.orchaldir.gm.utils.redux.Reducer

val CHARACTER_REDUCER: Reducer<CharacterAction, State> = { state, action ->
    when (action) {
        // character
        is DeleteCharacter -> deleteElement(state, action.id, State::canDeleteCharacter)
        is UpdateCharacter -> UPDATE_CHARACTER(state, action)
        is UpdateAppearance -> UPDATE_APPEARANCE(state, action)
        is UpdateEquipmentOfCharacter -> UPDATE_EQUIPMENT_MAP(state, action)
        is UpdateRelationships -> UPDATE_RELATIONSHIPS(state, action)
        // character templates
        is DeleteCharacterTemplate -> deleteElement(state, action.id, State::canDeleteCharacterTemplate)
        is UpdateCharacterTemplate -> UPDATE_CHARACTER_TEMPLATE(state, action)
        // statistic
        is CreateStatistic -> CREATE_STATISTIC(state, action)
        is DeleteStatistic -> deleteElement(state, action.id, State::canDeleteStatistic)
        is UpdateStatistic -> UPDATE_STATISTIC(state, action)
    }
}
