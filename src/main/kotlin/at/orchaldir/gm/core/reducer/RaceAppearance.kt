package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE_APPEARANCE: Reducer<CreateRaceAppearance, State> = { state, _ ->
    val character = RaceAppearance(state.getRaceAppearanceStorage().nextId)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().add(character)))
}

val DELETE_RACE_APPEARANCE: Reducer<DeleteRaceAppearance, State> = { state, action ->
    require(state.getRaceAppearanceStorage().getSize() > 1) { "Cannot delete the last race appearance" }
    //require(state.getCharacters(action.id).isEmpty()) { "Race ${action.id.value} is used by characters" }

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().remove(action.id)))
}

val UPDATE_RACE_APPEARANCE: Reducer<UpdateRaceAppearance, State> = { state, action ->
    state.getRaceAppearanceStorage().require(action.race.id)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().update(action.race)))
}