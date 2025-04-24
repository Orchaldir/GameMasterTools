package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.HasComplexStartDate
import at.orchaldir.gm.core.selector.time.date.convertDateToDefault
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
    val date: Date = Year(0),
) : Element<PeriodicalIssueId>, HasComplexStartDate {

    override fun id() = id

    override fun name(state: State): String {
        val periodical = state.getPeriodicalStorage().getOrThrow(periodical)

        return periodical.name(state) + " (" + dateAsName(state, periodical) + ")"
    }

    fun dateAsName(state: State): String {
        val periodical = state.getPeriodicalStorage().getOrThrow(periodical)

        return dateAsName(state, periodical)
    }

    private fun dateAsName(state: State, periodical: Periodical): String {
        val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)

        return display(calendar, date)
    }

    override fun startDate(state: State): Date? {
        val periodical = state.getPeriodicalStorage().getOrThrow(periodical)
        val calendar = state.getCalendarStorage().getOrThrow(periodical.calendar)

        return periodical.startDate()?.let { state.convertDateToDefault(calendar, it) }
    }

}