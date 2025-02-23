package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.calendar.*
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
