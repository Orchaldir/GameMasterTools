package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CloneRace
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.CreatedRace
import at.orchaldir.gm.core.model.race.ModifiedRace
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE: Reducer<CreateRace, State> = { state, _ ->
    val character = Race(state.getRaceStorage().nextId)

    noFollowUps(state.updateStorage(state.getRaceStorage().add(character)))
}

val CLONE_RACE: Reducer<CloneRace, State> = { state, action ->
    val original = state.getRaceStorage().getOrThrow(action.id)
    val cloneId = state.getRaceStorage().nextId
    val clone = original.copy(id = cloneId, name = "Clone ${cloneId.value}")

    noFollowUps(state.updateStorage(state.getRaceStorage().add(clone)))
}

val DELETE_RACE: Reducer<DeleteRace, State> = { state, action ->
    state.getRaceStorage().require(action.id)
    require(state.getRaceStorage().getSize() > 1) { "Cannot delete the last race" }
    require(state.getCharacters(action.id).isEmpty()) { "Race ${action.id.value} is used by characters" }

    noFollowUps(state.updateStorage(state.getRaceStorage().remove(action.id)))
}

val UPDATE_RACE: Reducer<UpdateRace, State> = { state, action ->
    val race = action.race
    state.getRaceStorage().require(race.id)
    checkDate(state, race.startDate(), "Race")
    checkLifeStages(state, race.lifeStages)
    checkOrigin(state, race)

    noFollowUps(state.updateStorage(state.getRaceStorage().update(race)))
}

fun checkLifeStages(state: State, lifeStages: LifeStages) {
    state.getRaceAppearanceStorage().require(lifeStages.getRaceAppearance())

    when (lifeStages) {
        is DefaultAging -> {
            require(lifeStages.maxAges.size == DefaultLifeStages.entries.size) { "Invalid number of max ages!" }
            checkMaxAge(lifeStages.getAllLifeStages())
        }

        is SimpleAging -> checkMaxAge(lifeStages.lifeStages)

        is ImmutableLifeStage -> doNothing()
    }
}

private fun checkMaxAge(lifeStages: List<LifeStage>) {
    var lastMaxAge = 0

    lifeStages.withIndex().forEach {
        require(it.value.maxAge > lastMaxAge) { "Life Stage ${it.value.name}'s max age must be greater than the previous stage!" }
        lastMaxAge = it.value.maxAge
    }
}

fun checkOrigin(state: State, race: Race) {
    when (race.origin) {
        is CreatedRace -> validateCreator(state, race.origin.creator, race.id, race.origin.date, "Creator")
        is ModifiedRace -> validateCreator(state, race.origin.modifier, race.id, race.origin.date, "Modifier")
        else -> doNothing()
    }
}
