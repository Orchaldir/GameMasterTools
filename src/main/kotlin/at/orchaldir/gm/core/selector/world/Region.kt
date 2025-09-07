package at.orchaldir.gm.core.selector.world

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.hasNoHasPositionsIn

fun State.canDeleteRegion(region: RegionId) = getTowns(region).isEmpty()
        && getSubRegions(region).isEmpty()
        && getCharactersLivingIn(region).isEmpty()
        && getCharactersPreviouslyLivingIn(region).isEmpty()
        && hasNoHasPositionsIn(region)

fun State.getRegions(type: RegionDataType) = getRegionStorage()
    .getAll()
    .filter { it.data.getType() == type }

fun State.getRegions(town: TownMapId) = getRegionsIds(town)
    .map { getRegionStorage().getOrThrow(it) }

fun State.getRegionsContaining(material: MaterialId) = getRegionStorage()
    .getAll()
    .filter { it.resources.contains(material) }

fun State.getRegionsCreatedBy(battle: BattleId) = getRegionStorage()
    .getAll()
    .filter { it.data.isCreatedBy(battle) }

fun State.getRegionsCreatedBy(catastrophe: CatastropheId) = getRegionStorage()
    .getAll()
    .filter { it.data.isCreatedBy(catastrophe) }

fun State.getRegionsIds(town: TownMapId) = getTownMapStorage()
    .getOrThrow(town)
    .map.tiles.mapNotNull { it.terrain.getMountain() }
    .distinct()

fun State.getSubRegions(region: RegionId) = getRegionStorage()
    .getAll()
    .filter { it.position.isIn(region) }

