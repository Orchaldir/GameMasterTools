package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.economy.HasEconomy
import at.orchaldir.gm.core.model.economy.UndefinedEconomy
import at.orchaldir.gm.core.model.realm.population.HasPopulation
import at.orchaldir.gm.core.model.realm.population.Population
import at.orchaldir.gm.core.model.realm.population.UndefinedPopulation
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.realm.validateTown
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.AreaLookup
import at.orchaldir.gm.utils.math.unit.CalculatedArea
import at.orchaldir.gm.utils.math.unit.HasArea
import kotlinx.serialization.Serializable

const val TOWN_TYPE = "Town"
val ALLOWED_VITAL_STATUS_FOR_TOWN = ALLOWED_VITAL_STATUS_FOR_REALM
val ALLOWED_CAUSES_OF_DEATH_FOR_TOWN = ALLOWED_CAUSES_OF_DEATH_FOR_REALM

@JvmInline
@Serializable
value class TownId(val value: Int) : Id<TownId> {

    override fun next() = TownId(value + 1)
    override fun type() = TOWN_TYPE
    override fun value() = value

}

@Serializable
data class Town(
    val id: TownId,
    val name: Name = Name.init(id),
    val title: NotEmptyString? = null,
    val founder: Reference = UndefinedReference,
    val date: Date? = null,
    val status: VitalStatus = Alive,
    val owner: History<RealmId?> = History(null),
    val area: AreaLookup = CalculatedArea,
    val population: Population = UndefinedPopulation,
    val economy: Economy = UndefinedEconomy,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<TownId>, Creation, HasArea, HasDataSources, HasEconomy, HasPopulation, HasPosition,
    HasVitalStatus {

    override fun id() = id
    override fun name() = name.text

    override fun area() = area
    override fun useDistrictsForAreaCalculation() = true
    override fun creator() = founder
    override fun economy() = economy
    override fun population() = population
    override fun position() = if (owner.current != null) {
        InRealm(owner.current)
    } else {
        UndefinedPosition
    }

    override fun sources() = sources
    override fun startDate(state: State) = date
    override fun vitalStatus() = status
    override fun validate(state: State) = validateTown(state, this)
}