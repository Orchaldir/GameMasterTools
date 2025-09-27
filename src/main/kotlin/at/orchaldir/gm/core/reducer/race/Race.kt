package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.MAX_RACE_HEIGHT
import at.orchaldir.gm.core.model.race.MIN_RACE_HEIGHT
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.aging.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.checkDistance

fun validateLifeStages(state: State, lifeStages: LifeStages) {
    state.getRaceAppearanceStorage().require(lifeStages.getRaceAppearance())

    when (lifeStages) {
        is DefaultAging -> {
            require(lifeStages.maxAges.size == DefaultLifeStages.entries.size) { "Invalid number of max ages!" }
            validateMaxAge(lifeStages.getAllLifeStages())
        }

        is SimpleAging -> validateMaxAge(lifeStages.lifeStages)

        is ImmutableLifeStage -> doNothing()
    }
}

fun validateHeight(race: Race) {
    checkDistance(race.height.center, "height", MIN_RACE_HEIGHT, MAX_RACE_HEIGHT)
}

private fun validateMaxAge(lifeStages: List<LifeStage>) {
    var lastMaxAge = 0

    lifeStages.withIndex().forEach {
        require(it.value.maxAge > lastMaxAge) { "Life Stage ${it.value.name}'s max age must be greater than the previous stage!" }
        lastMaxAge = it.value.maxAge
    }
}

