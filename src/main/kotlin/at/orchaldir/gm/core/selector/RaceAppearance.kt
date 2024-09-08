package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId

fun State.canDelete(race: RaceAppearanceId) = true