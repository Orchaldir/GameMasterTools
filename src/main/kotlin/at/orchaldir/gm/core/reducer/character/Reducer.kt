package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CharacterAction
import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.action.UpdateRelationships
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val CHARACTER_REDUCER: Reducer<CharacterAction, State> = { state, action ->
    when (action) {
        // character
        is UpdateAppearance -> UPDATE_APPEARANCE(state, action)
        is UpdateRelationships -> UPDATE_RELATIONSHIPS(state, action)
    }
}
