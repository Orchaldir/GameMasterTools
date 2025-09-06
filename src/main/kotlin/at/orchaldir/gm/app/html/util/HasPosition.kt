package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.util.getBusinessesIn
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>> HtmlBlockTag.showLocalElements(
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    val buildings = state.getBuildingsIn(id)
    val businesses = state.getBusinessesIn(id)

    if (buildings.isEmpty() && businesses.isEmpty()) {
        return
    }

    showDetails("Local Elements", true) {
        fieldList(call, state, buildings)
        fieldList(call, state, businesses)
    }
}
