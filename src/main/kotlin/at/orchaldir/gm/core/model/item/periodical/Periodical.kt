package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
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
    val ownership: History<Owner> = History(UndefinedOwner),
    val language: LanguageId = LanguageId(0),
    val calendar: CalendarId = CalendarId(0),
    val date: Date? = null,
    val frequency: PublicationFrequency = PublicationFrequency.Daily,
) : ElementWithSimpleName<PeriodicalId>, HasOwner, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun owner() = ownership
    override fun startDate() = date

}