package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.app.html.util.showBeliefStatus
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.TR
import kotlinx.html.td
import kotlinx.html.title

fun <ELEMENT : HasStartDate> createAgeColumn(
    state: State,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair("Age") { tdSkipZero(state.getAgeInYears(it.startDate())) }


fun <ELEMENT : HasBelief> createBeliefColumn(
    call: ApplicationCall,
    state: State,
): Pair<String, TR.(ELEMENT) -> Unit> =
    Pair("Belief") { td { showBeliefStatus(call, state, it.belief().current, false) } }

fun <ELEMENT : Creation> createCreatorColumn(
    call: ApplicationCall,
    state: State,
    label: String = "Creator",
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { td { showReference(call, state, it.creator(), false) } }

fun <ELEMENT : HasStartDate> createDateColumn(
    call: ApplicationCall,
    state: State,
    label: String = "Date",
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) {
    td {
        title = state.getAgeInYears(it.startDate())?.let { "$it years ago" } ?: ""
        showOptionalDate(call, state, it.startDate())
    }
}

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT : Element<ID0>> createIdColumn(
    call: ApplicationCall,
    state: State,
    label: String,
    convert: (ELEMENT) -> ID1,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { tdLink(call, state, convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createNameColumn(
    call: ApplicationCall,
    state: State,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair("Name") { tdLink(call, state, it) }

fun <ID : Id<ID>, ELEMENT : HasOrigin> createOriginColumn(
    call: ApplicationCall,
    state: State,
    createId: (Int) -> ID,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair("Origin") { td { showOrigin(call, state, it.origin(), createId) } }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createReferenceColumn(
    call: ApplicationCall,
    state: State,
    label: String,
    get: (ELEMENT) -> Reference,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { td { showReference(call, state, get(it), false) } }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createSkipZeroColumn(
    label: String,
    convert: (ELEMENT) -> Int?,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { tdSkipZero(convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>, T> createSkipZeroColumnFromCollection(
    label: String,
    convert: (ELEMENT) -> Collection<T>,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { tdSkipZero(convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createSkipZeroColumnForId(
    label: String,
    convert: (ID) -> Int?,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { tdSkipZero(convert(it.id())) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createStringColumn(
    label: String,
    convert: (ELEMENT) -> String?,
): Pair<String, TR.(ELEMENT) -> Unit> = Pair(label) { tdString(convert(it)) }
