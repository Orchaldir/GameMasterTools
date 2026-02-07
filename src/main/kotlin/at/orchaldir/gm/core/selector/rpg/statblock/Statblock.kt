package at.orchaldir.gm.core.selector.rpg.statblock

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.RaceLookup
import at.orchaldir.gm.core.model.race.UndefinedRaceLookup
import at.orchaldir.gm.core.model.race.UseRace
import at.orchaldir.gm.core.model.race.UseRaceRarityMap
import at.orchaldir.gm.core.model.rpg.statblock.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.utils.Id

fun State.getStatblock(base: Statblock, id: CharacterTemplateId): Statblock {
    val template = getCharacterTemplateStorage().getOrThrow(id)
    return getStatblock(base, template.statblock)
}

fun State.getStatblock(
    lookup: RaceLookup,
) = when (lookup) {
    UndefinedRaceLookup -> Statblock()
    is UseRace -> getStatblock(lookup.race)
    is UseRaceRarityMap -> getStatblock(lookup.map.getMostCommon())
}

fun State.getStatblock(
    race: RaceId,
) = getRaceStorage().getOrThrow(race).lifeStages.statblock()

fun State.getStatblock(base: Statblock, lookup: StatblockLookup): Statblock = when (lookup) {
    is UniqueStatblock -> lookup.statblock.applyTo(base)
    is UseStatblockOfTemplate -> getStatblock(base, lookup.template)
    is ModifyStatblockOfTemplate -> {
        val statblock = getStatblock(base, lookup.template)

        lookup.update.applyTo(statblock)
    }

    UndefinedStatblockLookup -> base
}

fun State.getStatblock(raceLookup: RaceLookup, lookup: StatblockLookup) =
    getStatblock(getStatblock(raceLookup), lookup)

fun State.getStatblock(raceId: RaceId, lookup: StatblockLookup): Statblock {
    val race = getRaceStorage().getOrThrow(raceId)

    return getStatblock(race.lifeStages.statblock(), lookup)
}

fun State.getStatblocksWith(statistic: StatisticId): List<Pair<Id<*>, Int>> {
    val statblocks = mutableListOf<Pair<Id<*>, Int>>()

    getCharacterTemplateStorage().getAll()
        .forEach { template ->
            template.race.races().forEach { raceId ->
                addStatblock(statblocks, statistic, getStatblock(raceId, template.statblock), template.id)
            }
        }

    getCharacterStorage().getAll()
        .forEach { character ->
            val statblock = getStatblock(character.race, character.statblock)

            addStatblock(statblocks, statistic, statblock, character.id)
        }

    return statblocks
}

private fun State.addStatblock(
    statblocks: MutableList<Pair<Id<*>, Int>>,
    statistic: StatisticId,
    statblock: Statblock,
    id: Id<*>,
) {
    statblock.resolve(this, statistic)?.let { value ->
        statblocks.add(Pair(id, value))
    }
}
