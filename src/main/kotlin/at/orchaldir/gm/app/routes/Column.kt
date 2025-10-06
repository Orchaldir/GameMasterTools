package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.html.optionalLink
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.app.html.util.displayVitalStatus
import at.orchaldir.gm.app.html.util.showBeliefStatus
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.html.util.showVitalStatus
import at.orchaldir.gm.app.routes.Column.Companion.tdColumn
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.selector.character.countKilledCharacters
import at.orchaldir.gm.core.selector.realm.countDestroyedRealms
import at.orchaldir.gm.core.selector.realm.countDestroyedTowns
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.TD
import kotlinx.html.TR
import kotlinx.html.td
import kotlinx.html.title

data class Column<T>(
    val header: List<String>,
    val converter: TR.(T) -> Unit, 
) {
    constructor(header: String, converter: TR.(T) -> Unit): this(listOf(header), converter)

    companion object {
        fun <T> tdColumn(header: String, converter: TD.(T) -> Unit) = tdColumn(listOf(header), converter)
        fun <T> tdColumn(header: List<String>, converter: TD.(T) -> Unit) = Column<T>(header, { td { converter(it) }})
    }
}

fun <ELEMENT : HasStartDate> createAgeColumn(
    state: State,
): Column<ELEMENT> = Column("Age") { tdSkipZero(state.getAgeInYears(it.startDate())) }


fun <ELEMENT : HasBelief> createBeliefColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> =
    tdColumn("Belief") { showBeliefStatus(call, state, it.belief().current, false) }

fun <ELEMENT : Creation> createCreatorColumn(
    call: ApplicationCall,
    state: State,
    label: String = "Creator",
): Column<ELEMENT> = tdColumn(label) { showReference(call, state, it.creator(), false) }

fun <ELEMENT : HasStartDate> createStartDateColumn(
    call: ApplicationCall,
    state: State,
    label: String = "Date",
) = createDateColumn<ELEMENT>(label, state, call, HasStartDate::startDate)

fun <ELEMENT : HasStartAndEndDate> createEndDateColumn(
    call: ApplicationCall,
    state: State,
    label: String,
) = createDateColumn<ELEMENT>(label, state, call, HasStartAndEndDate::endDate)

private fun <T> createDateColumn(
    label: String,
    state: State,
    call: ApplicationCall,
    getDate: (T) -> Date?,
): Column<T> = tdColumn(label) {
    val date = getDate(it)
    title = state.getAgeInYears(date)?.let { "$it years ago" } ?: ""
    showOptionalDate(call, state, date)
}


fun <ID : Id<ID>, ELEMENT : Element<ID>> createDestroyedColumns(
    state: State,
): List<Column<ELEMENT>> = listOf(
    createSkipZeroColumnForId(listOf("Destroyed", "Realms"), state::countDestroyedRealms),
    createSkipZeroColumnForId(listOf("Destroyed", "Towns"), state::countDestroyedTowns),
    createSkipZeroColumnForId(listOf("Killed", "Characters"), state::countKilledCharacters),
)

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT : Element<ID0>> createIdColumn(
    call: ApplicationCall,
    state: State,
    label: String,
    convert: (ELEMENT) -> ID1?,
): Column<ELEMENT> = tdColumn(label) { optionalLink(call, state, convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createNameColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> = Column("Name") { tdLink(call, state, it) }

fun <ID : Id<ID>, ELEMENT : HasOrigin> createOriginColumn(
    call: ApplicationCall,
    state: State,
    createId: (Int) -> ID,
): Column<ELEMENT> = tdColumn("Origin") { showOrigin(call, state, it.origin(), createId) }

fun <ELEMENT : HasPopulation> createPopulationColumn(): Column<ELEMENT> =
    Column("Population") { tdSkipZero(it.population().getTotalPopulation()) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createReferenceColumn(
    call: ApplicationCall,
    state: State,
    label: String,
    get: (ELEMENT) -> Reference,
): Column<ELEMENT> = tdColumn(label) { showReference(call, state, get(it), false) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createSkipZeroColumn(
    label: String,
    convert: (ELEMENT) -> Int?,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>, T> createSkipZeroColumnFromCollection(
    label: String,
    convert: (ELEMENT) -> Collection<T>,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createSkipZeroColumnForId(
    label: String,
    convert: (ID) -> Int?,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it.id())) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createSkipZeroColumnForId(
    label: List<String>,
    convert: (ID) -> Int?,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it.id())) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createStringColumn(
    label: String,
    convert: (ELEMENT) -> String?,
): Column<ELEMENT> = Column(label) { tdString(convert(it)) }

fun <ELEMENT : HasVitalStatus> createVitalColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> =
    tdColumn("End") { displayVitalStatus(call, state, it.vitalStatus(), false) }