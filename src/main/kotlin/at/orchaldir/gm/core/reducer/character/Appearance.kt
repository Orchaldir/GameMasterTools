package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.reducer.util.checkIsInside
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_APPEARANCE: Reducer<UpdateAppearance, State> = { state, action ->
    val character = state.getCharacterStorage().getOrThrow(action.id)
    val race = state.getRaceStorage().getOrThrow(character.race)

    when (action.appearance) {
        is HeadOnly -> checkIsInside(race.height, action.appearance.height) { "Character's height is invalid!" }
        is HumanoidBody -> checkIsInside(race.height, action.appearance.height) { "Character's height is invalid!" }
        UndefinedAppearance -> doNothing()
    }

    val updated = character.copy(appearance = action.appearance)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}
