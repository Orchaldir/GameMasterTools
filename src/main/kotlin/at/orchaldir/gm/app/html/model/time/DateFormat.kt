package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectBool
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.html.parseBool
import at.orchaldir.gm.app.html.parseChar
import at.orchaldir.gm.app.html.selectChar
import at.orchaldir.gm.core.model.time.calendar.DateFormat
import at.orchaldir.gm.core.model.time.calendar.DateOrder
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show


fun HtmlBlockTag.showDateFormat(format: DateFormat) {
    h2 { +"Format" }

    field("Date Order", format.order)
    field("Date Separator", "\"${format.separator}\"")
    field("Display Month Names", format.displayMonthNames.toString())
}

// edit

fun FORM.editDateFormat(format: DateFormat) {
    h2 { +"Format" }

    selectValue("Date Order", combine(FORMAT, ORDER), DateOrder.entries, format.order)
    selectChar("Date Separator", format.separator, combine(FORMAT, SEPARATOR))
    selectBool("Display Month Names", format.displayMonthNames, combine(FORMAT, MONTH, NAME))
}

// parse

fun parseDateFormat(
    parameters: Parameters,
) = DateFormat(
    parse(parameters, combine(FORMAT, ORDER), DateOrder.DayMonthYear),
    parseChar(parameters, combine(FORMAT, SEPARATOR), '.'),
    parseBool(parameters, combine(FORMAT, MONTH, NAME)),
)
