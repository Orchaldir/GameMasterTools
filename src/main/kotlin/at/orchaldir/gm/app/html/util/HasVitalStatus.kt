package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.getDestroyedBy
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun <ID : Id<ID>> HtmlBlockTag.showDestroyed(
    call: ApplicationCall,
    state: State,
    destroyer: ID,
) {
    val businesses = getDestroyedBy(state.getBusinessStorage(), destroyer)
    val characters = getDestroyedBy(state.getCharacterStorage(), destroyer)
    val gods = getDestroyedBy(state.getGodStorage(), destroyer)
    val moons = getDestroyedBy(state.getMoonStorage(), destroyer)
    val organizations = getDestroyedBy(state.getOrganizationStorage(), destroyer)
    val realms = getDestroyedBy(state.getRealmStorage(), destroyer)
    val towns = getDestroyedBy(state.getSettlementStorage(), destroyer)

    if (businesses.isEmpty() && characters.isEmpty() && gods.isEmpty() && moons.isEmpty() && organizations.isEmpty() && realms.isEmpty() && towns.isEmpty()) {
        return
    }

    h2 { +"Destroyed" }

    fieldElements(call, state, businesses)
    fieldElements(call, state, characters)
    fieldElements(call, state, gods)
    fieldElements(call, state, moons)
    fieldElements(call, state, organizations)
    fieldElements(call, state, realms)
    fieldElements(call, state, towns)
}
