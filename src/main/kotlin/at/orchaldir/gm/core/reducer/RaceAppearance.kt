package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE_APPEARANCE: Reducer<CreateRaceAppearance, State> = { state, _ ->
    val character = RaceAppearance(state.getRaceAppearanceStorage().nextId)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().add(character)))
}

val CLONE_RACE_APPEARANCE: Reducer<CloneRaceAppearance, State> = { state, action ->
    val original = state.getRaceAppearanceStorage().getOrThrow(action.id)
    val cloneId = state.getRaceAppearanceStorage().nextId
    val clone = original.copy(id = cloneId, name = "Clone ${cloneId.value}")

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().add(clone)))
}

val DELETE_RACE_APPEARANCE: Reducer<DeleteRaceAppearance, State> = { state, action ->
    state.getRaceAppearanceStorage().require(action.id)
    require(state.canDelete(action.id)) { "Race Appearance ${action.id.value} cannot be deleted" }

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().remove(action.id)))
}

val UPDATE_RACE_APPEARANCE: Reducer<UpdateRaceAppearance, State> = { state, action ->
    state.getRaceAppearanceStorage().require(action.race.id)

    noFollowUps(state.updateStorage(state.getRaceAppearanceStorage().update(action.race)))
}