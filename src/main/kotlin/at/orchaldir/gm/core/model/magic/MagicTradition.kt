package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val MAGIC_TRADITION_TYPE = "Magic Tradition"

@JvmInline
@Serializable
value class MagicTraditionId(val value: Int) : Id<MagicTraditionId> {

    override fun next() = MagicTraditionId(value + 1)
    override fun type() = MAGIC_TRADITION_TYPE
    override fun value() = value

}

@Serializable
data class MagicTradition(
    val id: MagicTraditionId,
    val name: Name = Name.init("$MAGIC_TRADITION_TYPE ${id.value}"),
    val date: Date? = null,
    val founder: Creator = UndefinedCreator,
    val groups: Set<SpellGroupId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<MagicTraditionId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun startDate() = date
    override fun sources() = sources

}