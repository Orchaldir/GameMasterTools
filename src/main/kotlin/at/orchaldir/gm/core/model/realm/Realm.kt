package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.realm.population.HasPopulation
import at.orchaldir.gm.core.model.realm.population.Population
import at.orchaldir.gm.core.model.realm.population.UndefinedPopulation
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.realm.validateRealm
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.AreaLookup
import at.orchaldir.gm.utils.math.unit.CalculatedArea
import at.orchaldir.gm.utils.math.unit.HasArea
import kotlinx.serialization.Serializable

const val REALM_TYPE = "Realm"
val ALLOWED_VITAL_STATUS_FOR_REALM = setOf(
    VitalStatusType.Alive,
    VitalStatusType.Abandoned,
    VitalStatusType.Destroyed,
)
val ALLOWED_CAUSES_OF_DEATH_FOR_REALM = setOf(
    CauseOfDeathType.Battle,
    CauseOfDeathType.Catastrophe,
    CauseOfDeathType.War,
    CauseOfDeathType.Undefined,
)

@JvmInline
@Serializable
value class RealmId(val value: Int) : Id<RealmId> {

    override fun next() = RealmId(value + 1)
    override fun type() = REALM_TYPE
    override fun value() = value

}

@Serializable
data class Realm(
    val id: RealmId,
    val name: Name = Name.init(id),
    val founder: Reference = UndefinedReference,
    val date: Date? = null,
    val status: VitalStatus = Alive,
    val capital: History<TownId?> = History(null),
    val owner: History<RealmId?> = History(null),
    val currency: History<CurrencyId?> = History(null),
    val legalCode: History<LegalCodeId?> = History(null),
    val area: AreaLookup = CalculatedArea,
    val population: Population = UndefinedPopulation,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<RealmId>, Creation, HasArea, HasDataSources, HasPopulation, HasPosition, HasVitalStatus {

    override fun id() = id
    override fun name() = name.text

    override fun area() = area
    override fun useRealmsForAreaCalculation() = true
    override fun useTownsForAreaCalculation() = true
    override fun creator() = founder
    override fun population() = population
    override fun position() = if (owner.current != null) {
        InRealm(owner.current)
    } else {
        UndefinedPosition
    }

    override fun sources() = sources
    override fun startDate() = date
    override fun vitalStatus() = status

    override fun validate(state: State) = validateRealm(state, this)

}
