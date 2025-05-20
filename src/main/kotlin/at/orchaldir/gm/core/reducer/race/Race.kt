package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.core.action.CloneRace
import at.orchaldir.gm.core.action.CreateRace
import at.orchaldir.gm.core.action.DeleteRace
import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.race.*
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.checkDistance
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_RACE: Reducer<CreateRace, State> = { state, _ ->
    val character = Race(state.getRaceStorage().nextId)

    noFollowUps(state.updateStorage(state.getRaceStorage().add(character)))
}

val CLONE_RACE: Reducer<CloneRace, State> = { state, action ->
    val original = state.getRaceStorage().getOrThrow(action.id)
    val cloneId = state.getRaceStorage().nextId
    val clone = original.copy(id = cloneId, name = Name.init("Clone ${cloneId.value}"))

    noFollowUps(state.updateStorage(state.getRaceStorage().add(clone)))
}

val DELETE_RACE: Reducer<DeleteRace, State> = { state, action ->
    state.getRaceStorage().require(action.id)
    require(state.getRaceStorage().getSize() > 1) { "Cannot delete the last race" }
    validateCanDelete(state.countCharacters(action.id), action.id, "it is used by a character")

    noFollowUps(state.updateStorage(state.getRaceStorage().remove(action.id)))
}

val UPDATE_RACE: Reducer<UpdateRace, State> = { state, action ->
    val race = action.race
    state.getRaceStorage().require(race.id)
    validateRace(state, race)

    noFollowUps(state.updateStorage(state.getRaceStorage().update(race)))
}

fun validateRace(state: State, race: Race) {
    checkDate(state, race.startDate(), "Race")
    checkHeight(race)
    checkLifeStages(state, race.lifeStages)
    checkOrigin(state, race)
    state.getDataSourceStorage().require(race.sources)
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

private fun checkHeight(race: Race) {
    checkDistance(race.height.center, "height", MIN_RACE_HEIGHT, MAX_RACE_HEIGHT)
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
