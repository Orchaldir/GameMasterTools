package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE: Reducer<CreateRace, State> = { state, _ ->
    val character = Race(state.getRaceStorage().nextId)

    noFollowUps(state.copy(races = state.getRaceStorage().add(character)))
}

val DELETE_RACE: Reducer<DeleteRace, State> = { state, action ->
    require(state.getRaceStorage().getSize() > 1) { "Cannot delete the last race" }
    require(state.getCharacters(action.id).isEmpty()) { "Race ${action.id.value} is used by characters" }

    noFollowUps(state.copy(races = state.getRaceStorage().remove(action.id)))
}

val UPDATE_RACE: Reducer<UpdateRace, State> = { state, action ->
    state.getRaceStorage().require(action.race.id)

    noFollowUps(state.copy(races = state.getRaceStorage().update(action.race)))
}