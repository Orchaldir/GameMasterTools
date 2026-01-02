package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.realm.validateWarParticipants
import at.orchaldir.gm.core.reducer.realm.validateWarSides
import at.orchaldir.gm.core.reducer.realm.validateWarStatus
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
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
    val status: WarStatus = FinishedWar(),
    val sides: List<WarSide> = emptyList(),
    val participants: List<WarParticipant> = emptyList(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<WarId>, HasDataSources, HasStartAndEndDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate(state: State) = startDate
    override fun endDate() = when (status) {
        OngoingWar -> null
        is FinishedWar -> status.date ?: startDate
    }

    override fun validate(state: State) {
        validateHasStartAndEnd(state, this)
        validateWarParticipants(state, this)
        validateWarSides(this)
        validateWarStatus(state, this)
    }

    fun getSideName(index: Int) = sides.getOrNull(index)?.name?.text ?: "${index + 1}.Side"
    fun getSideIndices() = (0..<sides.size).toList()

}
