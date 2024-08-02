package at.orchaldir.gm.core.model.calendar

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable


@JvmInline
@Serializable
value class CalendarId(val value: Int) : Id<CalendarId> {

    override fun next() = CalendarId(value + 1)
    override fun value() = value

}

@Serializable
data class Calendar(
    val id: CalendarId,
    val name: String = "Calendar ${id.value}",
) : Element<CalendarId> {

    override fun id() = id

}