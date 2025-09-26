package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.core.action.UpdateRace
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.MAX_RACE_HEIGHT
import at.orchaldir.gm.core.model.race.MIN_RACE_HEIGHT
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOrigin
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.checkDistance
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

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
    checkOrigin(state, race.id, race.origin, race.date, ::RaceId)
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

