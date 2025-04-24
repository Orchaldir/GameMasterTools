package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.name.ComplexName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.time.date.convertDateToDefault
import at.orchaldir.gm.utils.Element
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
    val name: ComplexName = SimpleName("Periodical ${id.value}"),
    val founder: Creator = UndefinedCreator,
    val ownership: History<Owner> = History(UndefinedOwner),
    val language: LanguageId = LanguageId(0),
    val calendar: CalendarId = CalendarId(0),
    val frequency: PublicationFrequency = DailyPublication(),
) : Element<PeriodicalId>, Created, HasOwner, HasComplexStartDate {

    override fun id() = id
    override fun name(state: State) = name.resolve(state)

    override fun creator() = founder
    override fun owner() = ownership
    override fun startDate(state: State) = state.convertDateToDefault(calendar, frequency.getStartDate())

}