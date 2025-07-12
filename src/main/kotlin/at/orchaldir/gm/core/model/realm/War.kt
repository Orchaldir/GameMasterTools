package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val WAR_TYPE = "War"

@JvmInline
@Serializable
value class WarId(val value: Int) : Id<WarId> {

    override fun next() = WarId(value + 1)
    override fun type() = WAR_TYPE
    override fun value() = value

}

@Serializable
data class War(
    val id: WarId,
    val name: Name = Name.init(id),
    val startDate: Date? = null,
    val status: WarStatus = OngoingWar,
    val realms: Set<RealmId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<WarId>, HasDataSources, HasStartAndEndDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = startDate
    override fun endDate() = status.endDate()

}
