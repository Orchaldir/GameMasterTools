package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE: Reducer<CreateRace, State> = { state, _ ->
    val character = Race(state.getRaceStorage().nextId)

    noFollowUps(state.updateStorage(state.getRaceStorage().add(character)))
}

val DELETE_RACE: Reducer<DeleteRace, State> = { state, action ->
    require(state.getRaceStorage().getSize() > 1) { "Cannot delete the last race" }
    require(state.getCharacters(action.id).isEmpty()) { "Race ${action.id.value} is used by characters" }

    noFollowUps(state.updateStorage(state.getRaceStorage().remove(action.id)))
}

val UPDATE_RACE: Reducer<UpdateRace, State> = { state, action ->
    state.getRaceStorage().require(action.race.id)
    checkLifeStages(action.race.lifeStages)

    noFollowUps(state.updateStorage(state.getRaceStorage().update(action.race)))
}

fun checkLifeStages(lifeStages: LifeStages) {
    when (lifeStages) {
        is ComplexAging -> checkMaxAge(lifeStages.lifeStages)
        is SimpleAging -> checkMaxAge(lifeStages.lifeStages)

        is ImmutableLifeStage -> doNothing()
    }
}

private fun checkMaxAge(lifeStages: List<LifeStage>) {
    var lastMaxAge = 0
    lifeStages.withIndex().forEach {
        require(it.value.maxAge() > lastMaxAge) { "Life Stage ${it.index}'s max age most be greater than the previous stage!" }
        lastMaxAge = it.value.maxAge()
    }
}
