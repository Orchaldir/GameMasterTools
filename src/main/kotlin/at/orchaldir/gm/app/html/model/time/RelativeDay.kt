package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.DAY
import at.orchaldir.gm.app.MONTH
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEEK
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.parseDayIndex
import at.orchaldir.gm.app.html.model.selectDayIndex
import at.orchaldir.gm.app.html.model.selectMonthIndex
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.selectWithIndex
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.DaysType
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.holiday.*
import io.ktor.http.*
import kotlinx.html.BODY
import kotlinx.html.HtmlBlockTag

// show

fun BODY.showRelativeDate(
    label: String,
    calendar: Calendar,
    relativeDate: RelativeDate,
) {
    field(label, relativeDate.display(calendar))
}


// edit

fun HtmlBlockTag.selectRelativeDate(param: String, relativeDate: RelativeDate, calendar: Calendar) {
    selectValue("Relative Date", combine(param, TYPE), RelativeDateType.entries, relativeDate.getType(), true) { type ->
        when (type) {
            RelativeDateType.DayInMonth, RelativeDateType.DayInYear -> false
            RelativeDateType.WeekdayInMonth -> calendar.days.getType() == DaysType.DayOfTheMonth
        }
    }
    when (relativeDate) {
        is DayInMonth -> selectDayIndex(
            "Day",
            param,
            relativeDate.dayIndex,
            0,
            calendar.getMinDaysPerMonth() - 1,
        )

        is DayInYear -> {
            selectMonthIndex("Month", param, calendar, relativeDate.monthIndex)
            selectDayIndex(
                "Day",
                param,
                calendar,
                relativeDate.monthIndex,
                relativeDate.dayIndex,
            )
        }

        is WeekdayInMonth -> {
            selectMonthIndex("Month", param, calendar, relativeDate.monthIndex)
            when (calendar.days) {
                DayOfTheMonth -> error("WeekdayInMonth doesn't support DayOfTheMonth!")
                is Weekdays -> selectWithIndex(
                    "Weekday",
                    combine(param, DAY),
                    calendar.days.weekDays
                ) { index, weekday ->
                    label = weekday.name
                    value = index.toString()
                    selected = relativeDate.weekdayIndex == index
                }
            }
            selectInt("Week", relativeDate.weekInMonthIndex, 0, 2, 1, combine(param, WEEK))
        }
    }
}


// parse

fun parseRelativeDate(parameters: Parameters, param: String): RelativeDate {
    return when (parse(parameters, combine(param, TYPE), RelativeDateType.DayInYear)) {
        RelativeDateType.DayInMonth -> DayInMonth(
            parseDayIndex(parameters, param),
        )

        RelativeDateType.DayInYear -> DayInYear(
            parseDayIndex(parameters, param),
            parseInt(parameters, combine(param, MONTH)),
        )

        RelativeDateType.WeekdayInMonth -> WeekdayInMonth(
            parseInt(parameters, combine(param, DAY)),
            parseInt(parameters, combine(param, WEEK)),
            parseInt(parameters, combine(param, MONTH)),
        )
    }
}
