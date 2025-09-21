package at.orchaldir.gm.core.model.character.statistic

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STATISTIC_TYPE = "Statistic"

@JvmInline
@Serializable
value class StatisticId(val value: Int) : Id<StatisticId> {

    override fun next() = StatisticId(value + 1)
    override fun type() = STATISTIC_TYPE
    override fun value() = value

}

@Serializable
data class Statistic(
    val id: StatisticId,
    val name: Name = Name.init("$STATISTIC_TYPE ${id.value}"),
    val short: Name? = null,
    val data: StatisticData = Attribute(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<StatisticId>, HasDataSources {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
}