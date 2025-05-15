package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TOWN_TYPE = "Town"

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
    val name: Name = Name.init("Town ${id.value}"),
    val foundingDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val map: TownMapId? = null,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<TownId>, Created, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = foundingDate

}