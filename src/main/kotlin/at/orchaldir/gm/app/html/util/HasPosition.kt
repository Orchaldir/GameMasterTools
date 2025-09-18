package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.info.observation.Observation
import at.orchaldir.gm.core.model.realm.Town
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
    town: Town,
    townMap: TownMap,
) = showLocalElementsInternal(
    call,
    state,
    state.getBuildingsIn(town.id).toSet() + state.getBuildingsIn(townMap.id).toSet(),
    state.getBusinessesIn(town.id).toSet() + state.getBusinessesIn(townMap.id).toSet(),
    emptySet(),
    state.getObservationsIn(town.id).toSet() + state.getObservationsIn(townMap.id).toSet(),
    state.getRegionsIn(town.id).toSet() + state.getRegionsIn(townMap.id).toSet(),
    state.getCharactersLivingIn(town.id).toSet() + state.getCharactersLivingIn(townMap.id).toSet(),
    state.getCharactersPreviouslyLivingIn(town.id).toSet() + state.getCharactersPreviouslyLivingIn(townMap.id).toSet(),
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
    state.getObservationsIn(id),
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
    observations: Collection<Observation>,
    regions: Collection<Region>,
    residents: Collection<Character>,
    formerResidents: Collection<Character>,
    worlds: Collection<World>,
) {
    if (buildings.isEmpty() && businesses.isEmpty() && moons.isEmpty() && observations.isEmpty() && regions.isEmpty() && residents.isEmpty() && formerResidents.isEmpty() && worlds.isEmpty()) {
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
        fieldList(call, state, buildings)
        fieldList(call, state, businesses)
        fieldList(call, state, moons)
        fieldList(call, state, observations)
        fieldList(call, state, regions)
        fieldList(call, state, "Residents", allResidents)
        fieldList(call, state, "Former Residents", allFormerResidents)
        fieldList(call, state, worlds)
    }
}
