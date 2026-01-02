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
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.economy.validateEconomy
import at.orchaldir.gm.core.reducer.realm.validatePopulation
import at.orchaldir.gm.core.reducer.util.checkPosition
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.AreaLookup
import at.orchaldir.gm.utils.math.unit.CalculatedArea
import at.orchaldir.gm.utils.math.unit.HasArea
import kotlinx.serialization.Serializable

const val DISTRICT_TYPE = "District"
val ALLOWED_DISTRICT_POSITIONS = listOf(
    PositionType.Undefined,
    PositionType.District,
    PositionType.Town,
)

@JvmInline
@Serializable
value class DistrictId(val value: Int) : Id<DistrictId> {

    override fun next() = DistrictId(value + 1)
    override fun type() = DISTRICT_TYPE
    override fun value() = value

}

@Serializable
data class District(
    val id: DistrictId,
    val name: Name = Name.init(id),
    val position: Position = UndefinedPosition,
    val foundingDate: Date? = null,
    val founder: Reference = UndefinedReference,
    val area: AreaLookup = CalculatedArea,
    val population: Population = UndefinedPopulation,
    val economy: Economy = UndefinedEconomy,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<DistrictId>, Creation, HasArea, HasDataSources, HasEconomy, HasPopulation, HasPosition,
    HasSimpleStartDate {

    override fun id() = id
    override fun name() = name.text

    override fun area() = area
    override fun useDistrictsForAreaCalculation() = true
    override fun creator() = founder
    override fun economy() = economy
    override fun population() = population
    override fun position() = position
    override fun sources() = sources
    override fun startDate() = foundingDate

    override fun validate(state: State) {
        checkPosition(state, position, "position", null, ALLOWED_DISTRICT_POSITIONS)
        validateCreator(state, founder, id, foundingDate, "founder")
        validatePopulation(state, population)
        validateEconomy(state, economy)
    }

}