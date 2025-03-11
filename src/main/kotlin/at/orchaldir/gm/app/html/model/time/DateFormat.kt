package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.FORMAT
import at.orchaldir.gm.app.MONTH
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.ORDER
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectBool
import at.orchaldir.gm.app.html.selectText
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseBool
import at.orchaldir.gm.app.parse.parseString
import at.orchaldir.gm.core.model.time.calendar.DateFormat
import at.orchaldir.gm.core.model.time.calendar.DateOrder
import io.ktor.http.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.h2

// show


fun BODY.showDateFormat(format: DateFormat) {
    h2 { +"Format" }

    field("Date Order", format.order)
    field("Date Separator", "\"${format.separator}\"")
    field("Display Month Names", format.displayMonthNames.toString())
}

// edit

fun FORM.editDateFormat(format: DateFormat) {
    h2 { +"Format" }

    selectValue("Date Order", combine(FORMAT, ORDER), DateOrder.entries, format.order)
    selectText("Date Separator", format.separator.toString(), combine(FORMAT, ORDER), 1, 1)
    selectBool("Display Month Names", format.displayMonthNames, combine(FORMAT, MONTH, NAME))
}

// parse

fun parseDateFormat(
    parameters: Parameters,
) = DateFormat(
    parse(parameters, combine(FORMAT, ORDER), DateOrder.DayMonthYear),
    parseString(parameters, combine(FORMAT, ORDER), ".")[0],
    parseBool(parameters, combine(FORMAT, MONTH, NAME)),
)
