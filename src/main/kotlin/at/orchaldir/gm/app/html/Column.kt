package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.economy.displayIncome
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.displayAreaLookup
import at.orchaldir.gm.app.html.util.population.showCulturesOfPopulation
import at.orchaldir.gm.app.html.util.population.showPopulation
import at.orchaldir.gm.app.html.util.population.showRacesOfPopulation
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Equipment
import at.orchaldir.gm.core.model.rpg.combat.MeleeAttack
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.population.HasPopulation
import at.orchaldir.gm.core.selector.character.countKilledCharacters
import at.orchaldir.gm.core.selector.realm.countDestroyedRealms
import at.orchaldir.gm.core.selector.realm.countDestroyedTowns
import at.orchaldir.gm.core.selector.rpg.getMeleeWeaponType
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.core.selector.util.calculateArea
import at.orchaldir.gm.core.selector.util.calculateDensity
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.AreaUnit
import at.orchaldir.gm.utils.math.unit.HasArea
import io.ktor.server.application.*
import kotlinx.html.TD
import kotlinx.html.TR
import kotlinx.html.td
import java.util.Locale

data class Column<T>(
    val header: List<String>,
    val width: Int? = null,
    val converter: TR.(T) -> Unit,
) {
    constructor(header: String, width: Int? = null, converter: TR.(T) -> Unit) : this(listOf(header), width, converter)

    companion object {
        fun <T> tdColumn(header: String, width: Int? = null, converter: TD.(T) -> Unit) =
            tdColumn(listOf(header), width, converter)

        fun <T> tdColumn(header: List<String>, width: Int? = null, converter: TD.(T) -> Unit) =
            Column<T>(header, width) { td { converter(it) } }
    }
}

fun <ELEMENT : HasStartAndEndDate> createAgeColumn(
    state: State,
    label: String = "Age",
): Column<ELEMENT> = Column(label) { tdSkipZero(it.getAgeInYears(state)) }

fun <ID : Id<ID>, ELEMENT> createAreaColumn(
    state: State,
    unit: AreaUnit = state.data.largeAreaUnit,
): Column<ELEMENT> where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea = tdColumn("Area") {
    displayAreaLookup(it.area(), unit) {
        state.calculateArea(it)
    }
}

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
    label: String = "Start Date",
) = createDateColumn<ELEMENT>(label, state, call, HasStartDate::startDate)

fun <ELEMENT : HasStartAndEndDate> createEndDateColumn(
    call: ApplicationCall,
    state: State,
    label: String = "End Date",
) = createDateColumn<ELEMENT>(label, state, call, HasStartAndEndDate::endDate)

private fun <T> createDateColumn(
    label: String,
    state: State,
    call: ApplicationCall,
    getDate: (T) -> Date?,
): Column<T> = tdColumn(label) {
    val date = getDate(it)

    showTooltip(state.getAgeInYears(date)?.let { "$it years ago" } ?: "") {
        showOptionalDate(call, state, date)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> createDestroyedColumns(
    state: State,
): List<Column<ELEMENT>> = listOf(
    countColumnForId(listOf("Destroyed", "Realms"), state::countDestroyedRealms),
    countColumnForId(listOf("Destroyed", "Towns"), state::countDestroyedTowns),
    countColumnForId(listOf("Killed", "Characters"), state::countKilledCharacters),
)

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT : Element<ID0>> createIdColumn(
    call: ApplicationCall,
    state: State,
    label: String,
    convert: (ELEMENT) -> ID1?,
): Column<ELEMENT> = tdColumn(label) { optionalLink(call, state, convert(it)) }

fun createMeleeWeaponColumn(
    state: State,
    label: String,
    display: TD.(MeleeAttack) -> Unit,
): Column<Equipment> = tdColumn(label) {
    state.getMeleeWeaponType(it)
        ?.let { type ->
            showMultiLine(type.attacks) { attack ->
                this@tdColumn.display(attack)
            }
        }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> createNameColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> = Column("Name") { tdLink(call, state, it) }

fun <ID : Id<ID>, ELEMENT : HasOrigin> createOriginColumn(
    call: ApplicationCall,
    state: State,
    createId: (Int) -> ID,
): Column<ELEMENT> = tdColumn("Origin") { showOrigin(call, state, it.origin(), createId) }

fun <ELEMENT : HasOwner> createOwnerColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> = tdColumn("Owner") { showReference(call, state, it.owner().current, false) }

fun <ELEMENT : HasPopulation> createPopulationColumn(): Column<ELEMENT> =
    tdColumn("Population") { showPopulation(it.population()) }

fun <ID : Id<ID>, ELEMENT> createPopulationDensityColumn(
    state: State,
    unit: AreaUnit = state.data.largeAreaUnit,
): Column<ELEMENT> where
        ELEMENT : Element<ID>,
        ELEMENT : HasArea,
        ELEMENT : HasPopulation = tdColumn(listOf("Population", "Density")) {
    val density = state.calculateDensity(it, unit)

    if (density > 0.0) {
        +String.format(Locale.US, "%.1f", density)
    }
}

fun <ELEMENT : HasPopulation> createPopulationIncomeColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> =
    tdColumn(listOf("Avg", "Income")) { it.population().income()?.let { displayIncome(call, state, it) } }

fun <ELEMENT : HasPopulation> createCulturesOfPopulationColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> = tdColumn("Cultures") { showCulturesOfPopulation(call, state, it.population()) }

fun <ELEMENT : HasPopulation> createRacesOfPopulationColumn(
    call: ApplicationCall,
    state: State,
): Column<ELEMENT> = tdColumn("Races") { showRacesOfPopulation(call, state, it.population()) }

fun <ELEMENT : HasPosition> createPositionColumn(
    call: ApplicationCall,
    state: State,
    label: String = "Position",
) = tdColumn<ELEMENT>(label) { showPosition(call, state, it.position(), false) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> createReferenceColumn(
    call: ApplicationCall,
    state: State,
    label: String,
    get: (ELEMENT) -> Reference,
): Column<ELEMENT> = tdColumn(label) { showReference(call, state, get(it), false) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> countColumn(
    label: String,
    convert: (ELEMENT) -> Int?,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>, T> countCollectionColumn(
    label: String,
    convert: (ELEMENT) -> Collection<T>,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it)) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> countColumnForId(
    label: String,
    convert: (ID) -> Int?,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it.id())) }

fun <ID : Id<ID>, ELEMENT : Element<ID>> countColumnForId(
    label: List<String>,
    convert: (ID) -> Int?,
): Column<ELEMENT> = Column(label) { tdSkipZero(convert(it.id())) }

fun <ELEMENT : HasVitalStatus> createVitalColumn(
    call: ApplicationCall,
    state: State,
    showType: Boolean,
    label: String = "End",
): Column<ELEMENT> =
    tdColumn(label) { displayVitalStatus(call, state, it.vitalStatus(), showType) }