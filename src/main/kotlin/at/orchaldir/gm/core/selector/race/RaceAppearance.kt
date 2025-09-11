package at.orchaldir.gm.core.selector.race

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearance
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId

fun State.canDeleteRaceAppearance(appearance: RaceAppearanceId) = DeleteResult(appearance)
    .addElements(getRaces(appearance))

fun State.getRaceAppearance(character: Character): RaceAppearance {
    val race = getRaceStorage().getOrThrow(character.race)

    return getRaceAppearanceStorage().getOrThrow(race.lifeStages.getRaceAppearance())
}

fun State.getRaceAppearancesMadeOf(material: MaterialId) = getRaceAppearanceStorage()
    .getAll()
    .filter { it.contains(material) }

fun State.countRaceAppearancesMadeOf(material: MaterialId) = getRaceAppearanceStorage()
    .getAll()
    .count { it.contains(material) }