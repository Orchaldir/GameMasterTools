package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.UpdateAppearance
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.Appearance
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.reducer.util.checkIsInside
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_APPEARANCE: Reducer<UpdateAppearance, State> = { state, action ->
    val character = state.getCharacterStorage().getOrThrow(action.id)
    validateCharacterAppearance(state, action.appearance, character.race)

    val updated = character.copy(appearance = action.appearance)

    noFollowUps(state.updateStorage(state.getCharacterStorage().update(updated)))
}

fun validateCharacterAppearance(
    state: State,
    appearance: Appearance,
    raceId: RaceId,
) {
    val race = state.getRaceStorage().getOrThrow(raceId)

    when (appearance) {
        is HeadOnly -> checkIsInside(race.height, appearance.height) { "Character's height is invalid!" }
        is HumanoidBody -> checkIsInside(race.height, appearance.height) { "Character's height is invalid!" }
        UndefinedAppearance -> doNothing()
    }
}
