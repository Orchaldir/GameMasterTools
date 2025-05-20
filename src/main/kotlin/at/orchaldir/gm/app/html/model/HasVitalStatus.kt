package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasVitalStatus
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>> HtmlBlockTag.showDestroyed(
    call: ApplicationCall,
    state: State,
    destroyer: ID,
) {
    showDestroyed("Killed Characters", call, state, destroyer, state.getCharacterStorage())
    showDestroyed("Destroyed Realms", call, state, destroyer, state.getRealmStorage())
    showDestroyed("Destroyed Towns", call, state, destroyer, state.getTownStorage())
}

private fun <ID : Id<ID>, ELEMENT, DESTROYER : Id<DESTROYER>> HtmlBlockTag.showDestroyed(
    label: String,
    call: ApplicationCall,
    state: State,
    destroyer: DESTROYER,
    storage: Storage<ID, ELEMENT>,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasVitalStatus {
    fieldList(call, state, label, getDestroyedBy(storage, destroyer))
}
