package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId

fun State.canDelete(id: RaceAppearanceId) = getRaces(id).isEmpty()

fun State.getRaceAppearance(character: Character): RaceAppearance {
    val race = getRaceStorage().getOrThrow(character.race)

    return getRaceAppearanceStorage().getOrThrow(race.lifeStages.getRaceAppearance())
}