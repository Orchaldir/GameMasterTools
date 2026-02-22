package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.realm.Settlement
import at.orchaldir.gm.core.model.world.World
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLocalElements(
    call: ApplicationCall,
    state: State,
    settlement: Settlement,
    townMap: TownMap,
) = showLocalElementsInternal(
    call,
    state,
    state.getBuildingsIn(settlement.id).toSet() + state.getBuildingsIn(townMap.id).toSet(),
    state.getBusinessesIn(settlement.id).toSet() + state.getBusinessesIn(townMap.id).toSet(),
    emptySet(),
    state.getRegionsIn(settlement.id).toSet() + state.getRegionsIn(townMap.id).toSet(),
    state.getCharactersLivingIn(settlement.id).toSet() + state.getCharactersLivingIn(townMap.id).toSet(),
    state.getCharactersPreviouslyLivingIn(settlement.id).toSet() + state.getCharactersPreviouslyLivingIn(townMap.id).toSet(),
    emptySet(),
)

fun <ID : Id<ID>> HtmlBlockTag.showLocalElements(
    call: ApplicationCall,
    state: State,
    id: ID,
) = showLocalElementsInternal(
    call,
    state,
    state.getBuildingsIn(id),
    state.getBusinessesIn(id),
    state.getMoonsOf(id),
    state.getRegionsIn(id),
    state.getCharactersLivingIn(id),
    state.getCharactersPreviouslyLivingIn(id),
    state.getWorldsIn(id),
)

private fun HtmlBlockTag.showLocalElementsInternal(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
    businesses: Collection<Business>,
    moons: Collection<Moon>,
    regions: Collection<Region>,
    residents: Collection<Character>,
    formerResidents: Collection<Character>,
    worlds: Collection<World>,
) {
    if (buildings.isEmpty() && businesses.isEmpty() && moons.isEmpty() && regions.isEmpty() && residents.isEmpty() && formerResidents.isEmpty() && worlds.isEmpty()) {
        return
    }

    val buildingIds = buildings.map { it.id }
    val businessIds = businesses.map { it.id }
    val residentsInBuildings = state.getCharactersLivingIn(buildingIds)
    val residentsInBusinesses = state.getCharactersLivingIn(businessIds)
    val allResidents = (residents + residentsInBuildings + residentsInBusinesses).toSet()
    val formerResidentsInBuildings = state.getCharactersLivingIn(buildingIds)
    val formerResidentsInBusinesses = state.getCharactersPreviouslyLivingIn(businessIds)
    val allFormerResidents =
        (formerResidents + formerResidentsInBuildings + formerResidentsInBusinesses).toSet() - allResidents

    showDetails("Local Elements", true) {
        fieldElements(call, state, buildings)
        fieldElements(call, state, businesses)
        fieldElements(call, state, moons)
        fieldElements(call, state, regions)
        fieldElements(call, state, "Residents", allResidents)
        fieldElements(call, state, "Former Residents", allFormerResidents)
        fieldElements(call, state, worlds)
    }
}
