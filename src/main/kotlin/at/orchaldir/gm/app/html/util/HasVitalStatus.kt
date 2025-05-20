package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.thMultiLines
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasVitalStatus
import at.orchaldir.gm.core.selector.character.countKilledCharacters
import at.orchaldir.gm.core.selector.realm.countDestroyedRealms
import at.orchaldir.gm.core.selector.realm.countDestroyedTowns
import at.orchaldir.gm.core.selector.util.getDestroyedBy
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.TR

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

fun TR.thDestroyed() {
    thMultiLines(listOf("Destroyed", "Realms"))
    thMultiLines(listOf("Destroyed", "Towns"))
    thMultiLines(listOf("Killed", "Characters"))
}

fun <ID : Id<ID>> TR.tdDestroyed(
    state: State,
    id: ID,
) {
    tdSkipZero(state.countDestroyedRealms(id))
    tdSkipZero(state.countDestroyedTowns(id))
    tdSkipZero(state.countKilledCharacters(id))
}