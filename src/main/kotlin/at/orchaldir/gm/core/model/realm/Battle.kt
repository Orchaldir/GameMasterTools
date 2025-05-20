package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BATTLE_TYPE = "Battle"

@JvmInline
@Serializable
value class BattleId(val value: Int) : Id<BattleId> {

    override fun next() = BattleId(value + 1)
    override fun type() = BATTLE_TYPE
    override fun value() = value

}

@Serializable
data class Battle(
    val id: BattleId,
    val name: Name = Name.init("Battle ${id.value}"),
    val date: Date? = null,
    val war: WarId? = null,
    val participants: List<BattleParticipant> = emptyList(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<BattleId>, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = date

}
