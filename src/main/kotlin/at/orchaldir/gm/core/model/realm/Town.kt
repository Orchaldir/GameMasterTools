package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
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
    val name: Name = Name.init(id),
    val title: NotEmptyString? = null,
    val foundingDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val status: VitalStatus = Alive,
    val owner: History<RealmId?> = History(null),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<TownId>, Creation, HasDataSources, HasVitalStatus {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = foundingDate
    override fun vitalStatus() = status

}