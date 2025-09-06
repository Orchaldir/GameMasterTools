package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.util.getBusinessesIn
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
    state.getCharactersLivingIn(town.id).toSet() + state.getCharactersLivingIn(townMap.id).toSet(),
    state.getCharactersPreviouslyLivingIn(town.id).toSet() + state.getCharactersPreviouslyLivingIn(townMap.id).toSet(),
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
    state.getCharactersLivingIn(id),
    state.getCharactersPreviouslyLivingIn(id),
)

private fun HtmlBlockTag.showLocalElementsInternal(
    call: ApplicationCall,
    state: State,
    buildings: Collection<Building>,
    businesses: Collection<Business>,
    residents: Collection<Character>,
    formerResidents: Collection<Character>,
) {
    if (buildings.isEmpty() && businesses.isEmpty() && residents.isEmpty() && formerResidents.isEmpty()) {
        return
    }

    val residentsInBuildings = state.getCharactersLivingIn(buildings.map { it.id })
    val allResidents = residents.toSet() + residentsInBuildings.toSet()

    val formerResidentsInBuildings = state.getCharactersLivingIn(buildings.map { it.id })
    val allFormerResidents = formerResidents.toSet() + formerResidentsInBuildings.toSet() - allResidents

    showDetails("Local Elements", true) {
        fieldList(call, state, buildings)
        fieldList(call, state, businesses)
        fieldList(call, state, "Residents", allResidents)
        fieldList(call, state, "Former Residents", allFormerResidents)
    }
}
