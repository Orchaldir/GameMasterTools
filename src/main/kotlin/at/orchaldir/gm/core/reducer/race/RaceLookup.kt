package at.orchaldir.gm.core.reducer.race

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.RaceLookup

fun validateRaceLookup(state: State, lookup: RaceLookup) = state.getRaceStorage().require(lookup.races())

