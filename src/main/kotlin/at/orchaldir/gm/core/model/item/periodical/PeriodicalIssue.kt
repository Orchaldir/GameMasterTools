package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.selector.time.calendar.getCalendar
import at.orchaldir.gm.core.selector.time.date.display
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PERIODICAL_ISSUE_TYPE = "Periodical Issue"

@JvmInline
@Serializable
value class PeriodicalIssueId(val value: Int) : Id<PeriodicalIssueId> {

    override fun next() = PeriodicalIssueId(value + 1)
    override fun type() = PERIODICAL_ISSUE_TYPE
    override fun value() = value

}

@Serializable
data class PeriodicalIssue(
    val id: PeriodicalIssueId,
    val periodical: PeriodicalId = PeriodicalId(0),
    val date: Date? = null,
) : Element<PeriodicalIssueId>, HasStartDate {

    override fun id() = id

    override fun name(state: State): String {
        val periodical = state.getPeriodicalStorage().getOrThrow(periodical)

        return periodical.name(state) + " (" + dateAsName(state) + ")"
    }

    fun dateAsName(state: State): String {
        val calendar = state.getCalendar(periodical)
        return date?.let { display(calendar, it) } ?: "Unknown"
    }

    override fun startDate() = date

}