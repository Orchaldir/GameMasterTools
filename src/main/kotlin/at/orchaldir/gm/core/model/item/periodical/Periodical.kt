package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.util.checkOwnership
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.core.selector.item.periodical.getValidPublicationFrequencies
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val PERIODICAL_TYPE = "Periodical"

@JvmInline
@Serializable
value class PeriodicalId(val value: Int) : Id<PeriodicalId> {

    override fun next() = PeriodicalId(value + 1)
    override fun type() = PERIODICAL_TYPE
    override fun value() = value

}

@Serializable
data class Periodical(
    val id: PeriodicalId,
    val name: Name = Name.init(id),
    val ownership: History<Reference> = History(UndefinedReference),
    val language: LanguageId = LanguageId(0),
    val calendar: CalendarId = CalendarId(0),
    val date: Date? = null,
    val frequency: PublicationFrequency = PublicationFrequency.Daily,
) : ElementWithSimpleName<PeriodicalId>, HasOwner, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun owner() = ownership
    override fun startDate() = date

    override fun validate(state: State) {
        state.getPeriodicalStorage().require(id)
        state.getCalendarStorage().require(calendar)
        state.getLanguageStorage().require(language)
        validateDate(state, date, "Founding")
        checkOwnership(state, ownership, date)

        require(state.getValidPublicationFrequencies(calendar).contains(frequency)) {
            "The ${calendar.print()} doesn't support $frequency!"
        }
    }

}