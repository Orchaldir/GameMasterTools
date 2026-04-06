package at.orchaldir.gm.core.model.ecology.plant

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginType
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.model.util.origin.validateOriginType
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.core.reducer.util.validateOrigin
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PLANT_TYPE = "Plant"
val ALLOWED_PLANT_ORIGINS = listOf(
    OriginType.Created,
    OriginType.Evolved,
    OriginType.Modified,
    OriginType.Original,
    OriginType.Planar,
    OriginType.Undefined,
)

@JvmInline
@Serializable
value class PlantId(val value: Int) : Id<PlantId> {

    override fun next() = PlantId(value + 1)
    override fun type() = PLANT_TYPE
    override fun value() = value

}

@Serializable
data class Plant(
    val id: PlantId,
    val name: Name = Name.init(id),
    val date: Date? = null,
    val origin: Origin = UndefinedOrigin,
    val appearance: PlantAppearance = UndefinedPlantAppearance,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<PlantId>, HasDataSources, HasOrigin, HasStartDate {

    init {
        validateOriginType(origin, ALLOWED_PLANT_ORIGINS)
    }

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun origin() = origin
    override fun sources() = sources
    override fun startDate(state: State) = date

    override fun validate(state: State) {
        validateDate(state, date, "Plant")
        validateOrigin(state, id, origin, date, ::PlantId)
        appearance.validate(state)
        state.getDataSourceStorage().require(sources)
    }

}