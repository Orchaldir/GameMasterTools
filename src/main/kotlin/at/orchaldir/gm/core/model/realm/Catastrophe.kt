package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CATASTROPHE_TYPE = "Catastrophe"

@JvmInline
@Serializable
value class CatastropheId(val value: Int) : Id<CatastropheId> {

    override fun next() = CatastropheId(value + 1)
    override fun type() = CATASTROPHE_TYPE
    override fun value() = value

}

@Serializable
data class Catastrophe(
    val id: CatastropheId,
    val name: Name = Name.init("Catastrophe ${id.value}"),
    val startDate: Date? = null,
    val endDate: Date? = null,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<CatastropheId>, HasDataSources, HasStartDate {

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
