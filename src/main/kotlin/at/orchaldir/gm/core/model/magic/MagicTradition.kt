package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
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
    val name: Name = Name.init(id),
    val date: Date? = null,
    val founder: Reference = UndefinedReference,
    val groups: Set<SpellGroupId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<MagicTraditionId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun startDate() = date
    override fun sources() = sources

    override fun validate(state: State) {
        state.getSpellGroupStorage().require(groups)
        state.getDataSourceStorage().require(sources)
    }

}