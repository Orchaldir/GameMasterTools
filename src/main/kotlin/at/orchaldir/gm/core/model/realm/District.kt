package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val DISTRICT_TYPE = "District"

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
    val town: TownId = TownId(0),
    val foundingDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<DistrictId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = foundingDate

}