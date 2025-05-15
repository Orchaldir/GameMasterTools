package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.getCurrentDate
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
    val name: Name = Name.init("War ${id.value}"),
    val startDate: Date? = null,
    val endDate: Date? = null,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<WarId>, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = startDate

    fun getDuration(state: State): Duration {
        val calendar = state.getDefaultCalendar()

        return if (startDate != null && endDate != null) {
            calendar.getDuration(startDate, calendar.getEndDay(endDate))
        } else if (startDate != null) {
            calendar.getDuration(startDate, state.getCurrentDate())
        } else {
            Duration(0)
        }
    }

}
