package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.DateType
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.time.date.Month
import at.orchaldir.gm.core.model.time.date.Week
import at.orchaldir.gm.core.model.time.date.Year

enum class PublicationFrequency {
    Daily,
    Weekly,
    Monthly,
    Yearly;

    fun getDateOfIssue(number: Int): Date = when (this) {
        Daily -> Day(number)
        Weekly -> Week(number)
        Monthly -> Month(number)
        Yearly -> Year(number)
    }

    fun getValidDateTypes(calendar: Calendar): Set<DateType> {
        val default = calendar.getValidDateTypes()

        return when (this) {
            Daily -> default
            Weekly -> default - DateType.Day
            Monthly -> default - DateType.Day - DateType.Week
            Yearly -> default - DateType.Day - DateType.Week - DateType.Month
        }
    }
}